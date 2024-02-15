package shootingstar.stellaide.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shootingstar.stellaide.entity.container.ContainerType;
import shootingstar.stellaide.exception.CustomException;
import shootingstar.stellaide.exception.ErrorCode;

import java.io.InputStream;
import java.util.Properties;

import static shootingstar.stellaide.exception.ErrorCode.NOT_SUPPORT_IMG_TYPE;

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
        String filePath = containerPath + containerName;
        String output = executeCommand("rm -rfv " + filePath);
        log.info("deleteContainer output : {}", output);
    }

    private String executeCommand(String command) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        String output = "";

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
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    output += new String(tmp, 0, i);
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    log.info("exit-status: {} ", channel.getExitStatus());
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

        return output;
    }
}
