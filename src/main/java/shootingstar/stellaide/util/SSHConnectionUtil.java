package shootingstar.stellaide.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static shootingstar.stellaide.exception.ErrorCode.NOT_SUPPORT_IMG_TYPE;
import static shootingstar.stellaide.exception.ErrorCode.STORAGE_COMMEND_ERROR;

@Slf4j
@Service
public class SSHConnectionUtil {

    @Value("${storage.baseUrl}")
    private String baseUrl;

    @Value("${storage.host}")
    private String host;

    @Value("${storage.name}")
    private String username;

    @Value("${storage.password}")
    private String password;

    @Value("${storage.homePath}")
    private String homePath;

    @Value("${storage.profileImgPath}")
    private String profileImgPath;

    @Value("${storage.containerPath}")
    private String containerPath;

    @Value("${storage.javaOriginPath}")
    private String javaOriginPath;

    @Value("${storage.pythonOriginPath}")
    private String pythonOriginPath;

    @Value("${storage.springOriginPath}")
    private String springOriginPath;

//    @Value("${storage.reactOriginPath}")
//    private String reactOriginPath;

    public String listDirectory(String directory) {
        // `ls` 명령어 실행
        return executeCommand("ls -l " + directory);
    }

    public String getProfileImgUrl(String imgFileName) {
        return baseUrl + "/profileImg/" + imgFileName;
    }

    public void deleteProfileImg(String imgFileName) {
        String filePath = profileImgPath + imgFileName;
        String output = executeCommand("rm -fv " + filePath);
        log.info("deleteProfileImg output : {}", output);
    }

    public void uploadProfileImg(MultipartFile file, String remoteFileName) {
        String allowedContentType = "image/png";
        if (!allowedContentType.equals(file.getContentType())) {
            throw new CustomException(NOT_SUPPORT_IMG_TYPE);
        }

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;

        try {
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            // 원격 디렉토리로 변경
            sftpChannel.cd(profileImgPath);

            // 파일 업로드
            // MultipartFile의 getInputStream을 사용하여 파일 콘텐츠를 직접 전송
            sftpChannel.put(file.getInputStream(), remoteFileName);

        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(ErrorCode.STORAGE_ERROR);
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void createContainer(ContainerType type, String containerName) {
        String command = null;
        switch (type) {
            case JAVA -> {
                command = "cp -R " + javaOriginPath + " " + containerPath + containerName;
                break;
            }
            case PYTHON -> {
                command = "cp -R " + pythonOriginPath + " " + containerPath + containerName;
                break;
            }
            case SPRING -> {
                command = "cp -R " + springOriginPath + " " + containerPath + containerName;
                break;
            }
//            case REACT -> {
//                command = "cp -R " + reactOriginPath + " " + containerPath + containerName;
//                break;
//            }
        }
        if (command != null) {
            executeCommand(command);
        }
    }

    public void deleteContainer(String containerName) {
        String remotePath = containerPath + containerName;
        String output = executeCommand("rm -rfv " + remotePath);
        log.info("deleteContainer output : {}", output);
    }

    public String getContainerTree(String containerName) {
        String remotePath = containerPath + containerName;
        String command = "cd " + remotePath + " && " + "tree -f -p";
        log.info(command);
        return executeCommand(command);
    }

    public String getFileContent(String containerName, String filePath) {
        String remotePath = containerPath + containerName + "/" + filePath;
        String command = "cat " + remotePath;
        log.info(command);
        return executeCommand(command);
    }

    public void saveFile(String filePath, String fileName, String fileContent) {
//        String remotePath = containerPath + filePath;
//        String command = homePath + "shell/exist_file_echo.sh \"" + fileContent + "\" " + remotePath + " " + fileName;
//        log.info(command);
//
//        String output = executeCommand(command);
//        log.info(output.trim());
//        if (output.trim().equals("Not Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
        Path tempFilePath = null;
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        ChannelExec channelExec = null;
        String output = "";
        String error = "";
        int existStatus = 0;

        try {
            // 임시 파일 생성
            tempFilePath = Files.createTempFile("tempFileForScript", ".txt");
            log.info("Temporary file created at: " + tempFilePath.toString());
            Files.write(tempFilePath, fileContent.getBytes());

            // SSH 세션 설정
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            // 파일 전송
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            String pushPath = homePath + "tmp/" + fileName;
            sftpChannel.put(tempFilePath.toString(), pushPath);
            sftpChannel.disconnect();
            Files.deleteIfExists(tempFilePath);

            String remotePath = containerPath + filePath;
            // 쉘 스크립트 실행
            String command = homePath + "shell/exist_file_echo.sh " + pushPath + " " + remotePath + " "  + fileName;
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            channelExec.connect();

            // 명령 실행 결과 읽기
            InputStream in = channelExec.getInputStream();
            // 명령 실행 에러(표준 에러) 읽기
            InputStream err = channelExec.getErrStream();

            channelExec.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output += new String(tmp, 0, i);
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    error += new String(tmp, 0, i); // 에러 메시지도 output에 추가
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    if (channelExec.getExitStatus() != 0) {
                        existStatus = channelExec.getExitStatus();
                        log.info("exit-status: {} ", channelExec.getExitStatus());
                        log.info(error);
                    }
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(ErrorCode.STORAGE_ERROR);
        } finally {
            if (channelExec != null) channelExec.disconnect();
            if (session != null) session.disconnect();
        }

        if (existStatus != 0) {
            throw new CustomException(STORAGE_COMMEND_ERROR);
        }
//        return output;
        log.info(output);
    }

    public void createFile(String filePath, String fileName) {
        String remotePath = containerPath + filePath;
        String command = homePath + "shell/exist_file_touch.sh " + remotePath + " " + fileName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void createDirectory(String directoryPath) {
        String remotePath = containerPath + directoryPath;
        String command = "mkdir " + remotePath;
        log.info(command);

        executeCommand(command);
    }

    public void copyFile(String filePath, String copyPath, String fileName) {
        String remotePath = containerPath + filePath;
        String remoteCopyPath = containerPath + copyPath;
        String command = homePath + "shell/exist_file_cp.sh " + remotePath + " " + remoteCopyPath + " " + fileName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void copyDirectory(String directoryPath, String copyPath, String directoryName) {
        String remotePath = containerPath + directoryPath;
        String remoteCopyPath = containerPath + copyPath;
        String command = homePath + "shell/exist_dir_cp.sh " + remotePath + " " + remoteCopyPath + " " + directoryName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void moveFile(String currentPath, String movedPath, String fileName) {
        String remotePath = containerPath + currentPath;
        String remoteMovedPath = containerPath + movedPath;
        String command = homePath + "shell/exist_file_mv.sh " + remotePath + " " + remoteMovedPath + " " + fileName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void moveDirectory(String currentPath, String movedPath, String directoryName) {
        String remotePath = containerPath + currentPath;
        String remoteMovedPath = containerPath + movedPath;
        String command = homePath + "shell/exist_dir_mv.sh " + remotePath + " " + remoteMovedPath + " " + directoryName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void renameFile(String filePath, String fileName, String changeName) {
        String remotePath = containerPath + filePath;
        String command = homePath + "shell/exist_file_rename.sh " + remotePath + " " + fileName + " " + changeName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void renameDirectory(String filePath, String directoryName, String changeName) {
        String remotePath = containerPath + filePath;
        String command = homePath + "shell/exist_dir_rename.sh " + remotePath + " " + directoryName + " " + changeName;
        log.info(command);

        String output = executeCommand(command);
        log.info(output.trim());
        if (output.trim().equals("Exist")) throw new CustomException(STORAGE_COMMEND_ERROR);
    }

    public void deleteFile(String filePath) {
        String remotePath = containerPath + filePath;
        String command = "rm " + remotePath;
        log.info(command);

        executeCommand(command);
    }

    public void deleteDirectory(String directoryPath) {
        String remotePath = containerPath + directoryPath;
        String command = "rm -r " + remotePath;
        log.info(command);

        executeCommand(command);
    }

    public String executionFile(String containerName, String filePath, ContainerType type) {
        String remotePath = containerPath + containerName;
        String command = null;
        switch (type) {
            case JAVA -> {
                command = "shopt -s globstar && "
                        + "javac -encoding UTF-8 -d " + remotePath + "/.bin " + remotePath + "/src/**/*.java && "
                        + "java -Dfile.encoding=UTF-8 -cp " + remotePath + "/.bin " + filePath;
                log.info(command);
                break;
            }
            case PYTHON -> {
                command = "python3 " + remotePath + "/" + filePath;
                log.info(command);
                break;
            }
        }

        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        String output = "";
        String error = "";
        int existStatus = 0;

        try {
            // 세션 설정
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // 호스트 키 검증 생략
            session.setConfig(config);

            // 세션 연결
            session.connect();

            // 명령 실행을 위한 채널 열기
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            // 명령 실행 결과 읽기
            InputStream in = channel.getInputStream();
            // 명령 실행 에러(표준 에러) 읽기
            InputStream err = channel.getErrStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output += new String(tmp, 0, i);
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    error += new String(tmp, 0, i); // 에러 메시지도 output에 추가
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    if (channel.getExitStatus() != 0) {
                        existStatus = channel.getExitStatus();
                        log.info("exit-status: {} ", channel.getExitStatus());
                        log.info(error);
                    }
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(ErrorCode.STORAGE_ERROR);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }

        if (existStatus != 0) {
            return error;
        }

        return output;
    }

    public String executionSpring(String containerName) {
        String remotePath = containerPath + containerName;
        String command = homePath + "start_spring.sh " + remotePath;
        log.info(command);
        String output = executeCommand(command);
        log.info(output);

        return baseUrl + "/logs/" + containerName + "/nohub.out";
    }

    public void stopSpringContainer(String containerName) {
        String remotePath = containerPath + containerName;
        String command = homePath + "stop_spring.sh " + remotePath;
        log.info(command);
        String output = executeCommand(command);
        log.info(output);
    }

    private String executeCommand(String command) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        String output = "";
        String error = "";
        int existStatus = 0;

        try {
            // 세션 설정
            session = jsch.getSession(username, host, 22);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no"); // 호스트 키 검증 생략
            session.setConfig(config);

            // 세션 연결
            session.connect();

            // 명령 실행을 위한 채널 열기
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);

            // 명령 실행 결과 읽기
            InputStream in = channel.getInputStream();
            // 명령 실행 에러(표준 에러) 읽기
            InputStream err = channel.getErrStream();

            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output += new String(tmp, 0, i);
                }
                while (err.available() > 0) {
                    int i = err.read(tmp, 0, 1024);
                    if (i < 0) break;
                    error += new String(tmp, 0, i); // 에러 메시지도 output에 추가
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    if (channel.getExitStatus() != 0) {
                        existStatus = channel.getExitStatus();
                        log.info("exit-status: {} ", channel.getExitStatus());
                        log.info(error);
                    }
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new CustomException(ErrorCode.STORAGE_ERROR);
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }

        if (existStatus != 0) {
            throw new CustomException(STORAGE_COMMEND_ERROR);
        }
        return output;
    }
}
