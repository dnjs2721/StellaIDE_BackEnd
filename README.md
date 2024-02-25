<div align="center">

# 🌟 StellarIDE

### 클라우드 기반 웹 IDE 플랫폼

[![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.2-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-DC382D?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?style=flat-square)](https://stomp.github.io/)

</div>

<br/>

## 📌 프로젝트 소개

StellarIDE는 **브라우저에서 바로 코드를 작성하고 실행할 수 있는 클라우드 웹 IDE 플랫폼**입니다.  
JSch 라이브러리를 활용해 원격 서버에 SSH로 연결하고, WebSocket으로 터미널 출력을 실시간 스트리밍합니다.

> 🏫 구름톤 트레이닝 풀스택 개발자 양성과정 3기 팀 프로젝트 (2024.02 – 2024.02)

<br/>

## 🏗️ 서비스 아키텍처

```
┌────────────────────────────────────────────────────────────────┐
│                     Web Browser                                │
│              (TypeScript / React 프론트엔드)                      │
└─────────────┬──────────────────┬─────────────────────────── ───┘
              │  REST API        │  WebSocket (STOMP)
              ▼                  ▼
┌───────────────────────────────────────────────────────────────── ┐
│                    Spring Boot 3.2 (Java 17)                     │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐     │
│  │                     인증 / 보안 레이어                      │     │
│  │   JWT (jjwt 0.11.5)  +  Spring Security  +  이메일 인증    │     │
│  └────────────────────────────┬────────────────────────────┘     │
│                               │                                  │
│  ┌────────────────────────────▼──────────────────────────── ┐    │
│  │                   비즈니스 로직 레이어                        │    │
│  │                                                          │    │
│  │  ┌──────────────────┐    ┌────────────────────────────┐  │    │
│  │  │   JSch (SSH)     │    │   WebSocket + STOMP        │  │    │
│  │  │  원격 서버 연결      │───▶│   실시간 터미널 스트리밍        │  │    │
│  │  │  명령 실행 전달      │    │   코드 실행 결과 전송          │  │    │
│  │  └──────────────────┘    └────────────────────────────┘  │    │
│  │                                                          │    │
│  │  ┌──────────────────────────────────────────────────┐    │    │
│  │  │         Spring Data JPA + QueryDSL 5.0           │    │    │
│  │  └──────────────────────────────────────────────────┘    │    │
│  └──────────────────────────────────────────────────────────┘    │
│                                                                  │
│  ┌────────────┐   ┌──────────────────┐   ┌────────────────┐      │
│  │  MySQL 8   │   │     Redis 7      │   │  SMTP 메일 서버  │      │
│  │   (RDB)    │   │  Token / Cache   │   │ 이메일 인증 코드   │      │
│  └────────────┘   └──────────────────┘   └────────────────┘      │
└───────────────────────────────────────────────────────────────── ┘
              │
              ▼  SSH (JSch)
┌─────────────────────────────┐
│       Remote Server         │
│     코드 실행 환경             │
└─────────────────────────────┘
```

### 🖥️ SSH 터미널 스트리밍 흐름

```
[Browser]
    │  WebSocket 연결 수립 (STOMP)
    │  터미널 명령 입력
    ▼
[WebSocket Handler]
    │  명령 수신
    ▼
[JSch SSH Session]
    │  원격 서버에 명령 전달
    ▼
[Remote Server]
    │  명령 실행 → InputStream 출력
    ▼
[Output Reader Thread]
    │  InputStream 폴링
    ▼
[WebSocket]
    │  실시간 스트리밍
    ▼
[Browser 터미널 화면]
```

<br/>

## 🛠️ 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.2.2, Spring Data JPA, Spring Security |
| **Query** | QueryDSL 5.0 |
| **Database** | MySQL 8.0, Redis 7.0 |
| **Auth** | JWT (jjwt 0.11.5), Spring Security |
| **실시간** | WebSocket, STOMP |
| **SSH** | JSch (com.github.mwiede:jsch:0.2.16) |
| **이메일** | JavaMail (SMTP), Thymeleaf 템플릿 |
| **빌드** | Gradle |

<br/>

## ✨ 주요 기능

| 기능 | 설명 |
|------|------|
| 🔐 인증 / 인가 | JWT Access + Refresh Token, Spring Security 필터 체인 |
| 📧 이메일 인증 | SMTP 인증 코드 발송, Redis TTL로 코드 만료 처리 |
| 💻 웹 IDE | 브라우저에서 코드 작성, 저장, 실행 |
| 🖥️ 실시간 터미널 | JSch SSH 연결 → WebSocket 터미널 출력 스트리밍 |
| 🗂️ 파일 트리 | 계층형 디렉토리 구조 조회 (QueryDSL 최적화) |

<br/>

## 📁 프로젝트 구조

```
src/main/java/
└── shootingstar/
    ├── auth/           # JWT, Spring Security
    ├── ide/            # WebSocket 터미널, JSch SSH 세션
    ├── project/        # 프로젝트 / 파일 트리 관리
    ├── member/         # 회원 도메인, 이메일 인증
    └── config/         # 전역 설정 (Redis, Security, WebSocket)
```

<br/>

## 👤 팀 구성 및 기여

- **백엔드 팀원 4명** | 백엔드 집중 개발
- **본인 담당:**
    - JSch 기반 SSH 연결 및 원격 서버 명령 실행 구현
    - WebSocket 실시간 터미널 스트리밍 구현 (SSH ↔ WebSocket 브리지)
    - JWT 인증 + 이메일 인증 시스템 설계 (Redis TTL 만료 흐름)
    - QueryDSL 기반 파일 트리 조회 API 구현