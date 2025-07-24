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
    * 📖 개요
    * 🤔 문제점
    * 💡 아이디어 / 해결 방안
    * 🚀 주요 기능
    * 🏗️ 프로젝트 구조
    * 📦 결과물
* 🛠️ **기술 스택**
* 💻 **설치 및 실행 방법**
    * ⚙️ 개발 환경 설정
    * 🚀 프로젝트 빌드 및 실행
* ☁️ **배포**
* 📄 **사용자 가이드**
* 📈 **ERD**
* 🧪 **테스트**
* 🚨 **트러블 슈팅**
* 🤝 **기여 방법**
* ⚖️ **라이선스**
* 🧑‍💻 **팀 정보**

## **📖 개요**

최근 빠르게 변화하는 개발 기술 트렌드 속에서 지속적인 학습과 성장은 개발자에게 필수적입니다. 많은 개발자들이 효과적인 학습을 위해 스터디를 선호하지만, 자신과 개발 수준 및 학습 목표가 일치하는 스터디 파트너를 찾는 데 어려움을 겪고 있습니다.
저 또한 비전공자로서 개발 학습을 시작하며 이와 같은 어려움을 직접 경험했습니다. 

특히 기존 스터디 매칭 플랫폼들은 기술 카테고리(예: Java, Python)나 개인의 개발 역량, 숙련도를 세밀하게 설정하고 필터링하는 기능이 부족하여, 나와 정말 잘 맞는 스터디나 파트너를 찾기가 매우 힘들었습니다. 
이는 비전공자뿐만 아니라 많은 개발자들이 겪는 공통적인 페인 포인트였습니다. 저는 이러한 개발자들의 페인 포인트를 해결하고, 개인의 성장과 생산성을 극대화할 수 있는 스터디 환경을 제공하고자 본 프로젝트를 기획했습니다.

&nbsp;
&nbsp;

## **🤔 문제점**
1.  **정보 부족 및 탐색의 어려움** **→**
    기존 스터디 플랫폼들을 사용해 보면, 스터디원들이 어떤 기술을 쓰는지, 개발 실력은 어느 정도인지, 뭘 목표로 하는지 자세히 알기가 너무 어려웠습니다. 필터링 기능도 없는 경우가 많아, 나한테 딱 맞는 스터디나 팀원을 찾으려면 엄청 헤매게 되었죠. 한마디로 **시작도 전에 지쳐버리기 일쑤**였습니다.

2.  **비효율적인 매칭 과정** **→**
대충 "스터디 할 사람?" 하고 모이는 경우가 많다 보니, 막상 시작하고 나면 서로 기대했던 게 다르거나 실력 차이가 너무 나는 경우가 허다했습니다. 특히 **직장인이나 아르바이트를 하는 경우, 기껏 마음에 드는 스터디를 찾아도 서로 시간을 맞추기 어려워** 참여가 힘들 때가 많았습니다. 결국 흐지부지되거나 중간에 그만두는 스터디가 많아서, 애써 찾은 스터디가 **아까운 시간 낭비로 이어지기도** 합니다.

3.  **비전공자의 진입 장벽** **→**
    내 실력이 어느 정도인지 객관적으로 파악하기도 어렵고, 기존 스터디에 들어가려 해도 내 배경을 설명하고 이해시키는 게 너무 힘들었습니다. 그러다 보니 괜히 위축되고, **스터디 참여 자체가 어렵게 느껴지기도** 합니다.

&nbsp;
&nbsp;

## 💡 아이디어 / 해결 방안 (Idea / Solution)

위에서 언급된 문제점들을 해결하고, 개발자들이 더욱 효과적으로 스터디하고 성장할 수 있는 환경을 제공하기 위해 다음과 같은 생각을 해봤습니다.

1.  👤 **개인 프로필 기반의 매칭 시스템**
    * 사용자가 자신의 **개발 수준, 선호 기술 스택(Java, Python 등), 학습 목표, 관심 분야, 그리고 가능한 스터디 시간대**를 상세하게 설정할 수 있는 개인 프로필을 제공.
    * 이 프로필 데이터를 기반으로 **최적의 스터디 방을 추천**하여, 처음부터 서로에게 잘 맞는 환경에서 스터디를 시작할 수 있도록 도움. 이를 통해 비효율적인 매칭과 시간 낭비를 최소화.

2.  ✅ **스터디 방 생성 시 조건 명시 및 매칭 연동:**
    * 스터디 방을 만들 때, 방장이 원하는 **기술 스택, 필요 역량 수준, 스터디 방식, 그리고 구체적인 스터디 시간** 등을 명확하게 기입할 수 있도록 함.
    * 이렇게 기입된 조건과 참여를 희망하는 스터디원의 프로필 내용을 비교하여 **높은 일치율을 가진 매칭**을 제공함으로써, 스터디 시작 전 기대치 불일치를 줄이고 스터디의 성공 가능성을 높임.

3.  🧑‍🤝‍🧑 **방장 양도 시스템 도입:**
    * 스터디 방장이 개인 사정으로 스터디를 지속하기 어려울 경우, **남아있는 스터디원 중 한 명에게 방장 권한을 양도**할 수 있는 시스템을 제공함.
    * 이를 통해 갑작스러운 방장의 이탈에도 스터디가 와해되지 않고, 나머지 스터디원들이 새로운 인원을 구하거나 스터디를 지속할 수 있도록 지원하여 **스터디의 안정적인 운영을 도움.**

4.  🤝 **친구 기능 및 지속적인 교류 환경:**
    * 스터디가 종료된 이후에도 스터디원들 간에 **지속적으로 교류할 수 있도록 친구 기능을 제공**함.
    * 이를 통해 스터디를 통해 형성된 네트워크를 유지하고, 새로운 스터디를 함께 시작하거나 개발 관련 정보를 공유하는 등 **장기적인 학습 및 성장 환경을 조성**함.
    
&nbsp;
### 해결책 요약
이 프로젝트는 개발자들이 스터디를 찾고 참여하면서 겪는 어려움들을 해결하는 데 집중했습니다. 사용자들의 자세한 프로필(기술, 실력, 목표, 가능한 시간 등)을 바탕으로 **가장 잘 맞는 스터디 팀원과 방을 꼼꼼하게 찾아드려서** 불필요하게 스터디를 헤매는 시간을 확 줄여주는 것이 목표입니다.

그리고 스터디 방을 만들 때부터 방장이 원하는 조건을 정확히 정하고, 이걸 매칭에 반영해서 **시작부터 서로 딱 맞는 스터디**가 되도록 도와줍니다. 혹시 스터디장이 갑자기 그만두더라도, **방장 권한을 다른 팀원에게 넘겨줄 수 있는 시스템**을 만들어서 스터디가 중간에 멈추는 일 없이 꾸준히 이어질 수 있게 만들어봤어요.

마지막으로, 스터디가 끝나도 서로 계속 연락하며 지낼 수 있는 **친구 기능**을 제공해서, 스터디를 통해 생긴 소중한 인연들이 길게 이어지고 함께 성장할 수 있는 환경을 만들어줄 겁니다.


