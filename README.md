# [![Deploy Spring Boot to AWS](https://github.com/tickling1/switching-project/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/tickling1/switching-project/actions/workflows/gradle.yml)
<img width="2236" height="1258" alt="image" src="https://github.com/user-attachments/assets/d0709a16-a01d-4c33-9005-de4ae7c192dc" />

### **Languages**

![Static Badge](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white) ![Static Badge](https://img.shields.io/badge/SQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### **Technologies**
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white) ![QueryDSL](https://img.shields.io/badge/QueryDSL-007396?style=for-the-badge&logo=apache-couchdb&logoColor=white) ![JPA](https://img.shields.io/badge/JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white) ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)
![Linux](https://img.shields.io/badge/Linux-FCC624?style=for-the-badge&logo=linux&logoColor=black) ![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=Amazon%20AWS&logoColor=white"/>

###
---

# **Table of Contents**

* ✨ **프로젝트 소개**
* ☁️ **인프라 구성 및 CI/CD 흐름**
* 🚨 **기술적 issue**
* 📈 **DB ERD**

###
---
## ✨ **프로젝트 소개**

최근 빠르게 변화하는 개발 기술 트렌드 속에서 지속적인 학습과 성장은 개발자에게 더 이상 선택이 아닌 필수적인 요소가 되었습니다. 새로운 언어와 프레임워크가 끊임없이 등장하고, 기존 기술 또한 빠른 주기로 업데이트되기 때문에, 개발자는 끊임없이 학습하고 경험을 쌓아야만 변화에 뒤처지지 않을 수 있습니다. 이러한 상황에서 많은 개발자들은 혼자 학습하는 것보다, 함께 공부하며 지식을 나누고 서로 자극을 주는 스터디를 선호하는 경우가 많습니다.

그러나 실제로 자신에게 맞는 스터디를 찾는 일은 생각보다 쉽지 않습니다. 단순히 같은 기술 스택(Java, Python, Spring 등)에 관심이 있다고 해서 학습 목표나 수준이 맞는 것은 아니기 때문입니다. 예를 들어, 같은 Java 스터디라고 하더라도 누군가는 입문자를 대상으로 기본 문법을 공부하고 싶어 하고, 또 다른 누군가는 대규모 프로젝트에서의 아키텍처 설계나 성능 최적화를 심도 있게 다루고 싶어할 수 있습니다. 이처럼 학습의 목표와 깊이가 다르다면, 같은 스터디에 참여하더라도 만족도가 크게 떨어지게 됩니다.

저 또한 비전공자로서 개발 학습을 시작하면서 이러한 어려움을 직접 경험했습니다. 처음에는 단순히 "Java 스터디"라는 이름만 보고 참여했지만, 실제로는 제 수준과 맞지 않아 따라가기 힘들거나, 반대로 제가 원하던 만큼 깊이 있는 토론이 이루어지지 않아 아쉬움을 느낀 경우가 많았습니다. 결국 "같은 기술을 공부한다"는 공통점만으로는 제대로 된 시너지를 내기 어렵다는 사실을 깨닫게 되었습니다.

특히 기존 스터디 매칭 플랫폼들을 살펴보면, 기술 카테고리만 제공하거나 단순한 자기소개 기반으로 매칭을 시도하는 경우가 대부분이었습니다. 개인의 개발 경험, 학습 목표, 선호하는 학습 방식(예: 프로젝트 기반, 문제 풀이 기반, 이론 중심 학습 등)을 세밀하게 설정하고 필터링할 수 있는 기능은 부족했습니다. 이로 인해 나와 정말 잘 맞는 스터디나 파트너를 찾기가 매우 힘들었고, 이는 단순히 저 같은 비전공자뿐만 아니라, 다양한 배경을 가진 개발자들이 공통적으로 겪는 페인 포인트라는 것을 알게 되었습니다.

저는 이러한 문제를 해결하고 싶었습니다. 단순히 "스터디를 연결해주는 플랫폼"을 넘어서, 개인의 학습 목표와 역량 수준에 맞는 최적의 매칭을 제공하고, 이를 통해 개발자가 더욱 효율적이고 즐겁게 성장할 수 있는 학습 환경을 만드는 것이 필요하다고 느꼈습니다. 이러한 문제의식에서 출발해, 개발자들의 학습 경험을 개선하고 개인의 성장과 생산성을 극대화할 수 있는 스터디 매칭 플랫폼을 기획하게 되었습니다.

---
## **☁️ 인프라 구성 및 CI/CD 흐름**
<img width="2236" height="1258" alt="시스템 다이어그램 최종본" src="https://github.com/user-attachments/assets/c0c008bc-f0fd-4ef7-8ea6-680ee9f13213" />

이번 프로젝트에서는 **GitHub Actions와 AWS를 활용한 자동화된 CI/CD 파이프라인**을 직접 구축했습니다. 단순히 배포만 자동화하는 것을 넘어, **서비스 중단 없이 안정적으로 배포할 수 있는 환경**을 만드는 데에 중점을 두었고,  혼자서 하나하나 설정하고 운영하면서 DevOps에 대한 실전 경험을 쌓을 수 있었습니다.

##### 구축 배경
- 매번 EC2에 접속해서 빌드하고 서버 재시작하는 작업을 자동화하고 싶었습니다.
- 배포 중 사용자에게 영향을 주지 않도록, 서비스가 **끊기지 않게 무중단 배포**를 구현해보고 싶었습니다.
- EC2, S3, RDS 같은 AWS 서비스를 직접 연동해보면서 **인프라 전반에 대한 이해도를 높이고 싶었습니다.**

##### 전체 구성 흐름

###### 1. GitHub Actions – CI/CD 자동화
- `main` 브랜치에 코드가 push되면, GitHub Actions가 자동으로 워크플로우를 실행합니다.
- 내부적으로는 Gradle로 프로젝트를 빌드하고, `.jar` 파일과 배포 스크립트를 묶어 `app.zip` 파일을 생성합니다.
- 만들어진 `app.zip`은 **S3 버킷**으로 업로드되며, 이후 배포에 사용됩니다.

###### 2. S3 – 배포 파일 저장소
- `app.zip` 파일은 S3에 업로드되고,
- CodeDeploy는 이 파일을 다운로드해서 EC2에 배포하는 데 활용합니다.

###### 3. CodeDeploy – 배포 자동화
- CodeDeploy는 `appspec.yml`을 기반으로 `clean-up.sh`, `start.sh` 같은 배포 스크립트를 실행합니다.
- 배포 과정은 다음과 같은 순서로 진행됩니다:
    1. 기존 파일 정리
    2. 새로운 서버 프로세스 실행 (기존 서버는 유지)
    3. **헬스 체크를 통과하면 nginx 설정을 바꿔 새 서버로 트래픽을 전환**
    4. 구버전 프로세스를 안전하게 종료

###### 4. 무중단 배포 – Nginx + 포트 스위칭 방식
- 한 대의 EC2 인스턴스에서 9090, 9091 포트를 번갈아 사용합니다.
    - 예: 현재 서버가 9090에서 돌고 있으면, 새 버전은 9091에서 실행됨
- 헬스 체크 성공 시 Nginx 설정을 바꿔 새 포트로 트래픽을 넘깁니다.
- 이렇게 하면 **사용자는 배포 도중에도 끊김 없이 서비스 이용이 가능**합니다.

###### 5. Amazon RDS – MySQL 데이터베이스
- 애플리케이션에서 사용하는 데이터는 전부 Amazon RDS (MySQL)에 저장됩니다.
- DB 연결 정보는 AWS Secrets Manager를 통해 안전하게 관리합니다.

## **🧠 배포 중 겪었던 문제 - 메모리 부족**
배포할 때 신버전과 구버전 서버가 동시에 실행되면서, **EC2 프리티어(메모리 1GB)의 메모리가 부족해지는 문제가 발생**했습니다.

###### ✅ 해결 방법 – Swap 메모리 추가
EC2 인스턴스에 Swap 공간을 추가해서, 부족한 메모리를 보완했습니다.

```bash
# 1GB 스왑 생성 및 활성화
sudo fallocate -l 1G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```
덕분에 2개의 Java 프로세스를 동시에 띄워도 문제가 발생하지 않게 되었고, 무중단 배포도 안정적으로 동작하게 되었습니다.
&nbsp;
###
---
## 🚨 **기술적 issue**
- [#1] N+1 문제 원인 및 해결 과정  
  https://madeprogame.tistory.com/384  

- [#2] 동시성 문제 원인 및 해결 과정  
  https://madeprogame.tistory.com/400  

- [#3] 서비스 레이어 테스트 커버리지  
  https://madeprogame.tistory.com/402  

- [#4] 사용자 로그인 처리(JWT, Session, RefreshToken)  
  https://madeprogame.tistory.com/403  

- [#5] CI/CD 구축기  
  https://madeprogame.tistory.com/406  

- [#6] 무중단 배포 전략 (Blue-Green, Rolling, Canary)  
  https://madeprogame.tistory.com/405  
###
---
## 📈 **DB ERD**

&nbsp;
&nbsp;
---





