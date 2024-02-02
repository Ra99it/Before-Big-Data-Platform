# Distributed-processing-and-cluster-operations-engineering


### 개요</b>
-------------------

<b> 분산 처리가 가능하고, 고 가용성을 유지 하도록 클러스터 운영을 할 수 있게 구축 해 본 서버 입니다.
직접 설계와 구축을 진행하면서, 다양한 기술을 적용시키는 것이 주요 목적입니다. </b>


<b>고 가용성(high availability, HA)</b> : 서버, 네트워크, 프로그램 등의 정보 시스템이 상당히 오랜 기간 동안 지속적으로 정상 운영이 가능한 성질을 말한다. 고(高)가용성이란 "가용성이 높다"는 뜻으로서, "절대 고장 나지 않음"을 의미한다.

<출처 : https://ko.wikipedia.org/wiki/%EA%B3%A0%EA%B0%80%EC%9A%A9%EC%84%B1 >

### 기술 아키텍처(v1.0) 
-------------------
![전체 아키텍처 drawio](https://github.com/Ra99it/Study_dataengineering/assets/122541545/4972be85-5e68-4617-8e93-0e485ebae2b2)

##### 1번 아키텍처 설명
Api Server와 prometheus Server의 설치된 node_exporter는 서버 자원 리소스 및 네트워크 정보를 메트릭으로 수집합니다. 수집된 메트릭을 Prometheus가 HTTP 통신을 통해 메트릭을 수집하게 되고, 시각화 없이는 수집된 메트릭을 사람의 눈으로 보고 판별하기는 어렵기 때문에 Grafana로 시각화로 보여주는 아키텍처 입니다. ELK(Elastic Search, Logstash, Kibana)스택과 같은 기능을 합니다. 즉 Observability의 개념을 구현했다고 생각하시면 되겠습니다.

Observability는 기본적인 모니터링과 비슷하지만 다른 의미입니다. 모니터링은 에러나 다운타임, 비정상 상태 등을 감지하기 위해서 이벤트, 메트릭 정보를 남기고 추적하는 것을 말합니다. 반면에 Observability는  IT시스템이 복잡해지면서 HW, OS, SW, Cloud infra등 모든 데이터를 활용해서 어플리케이션의 상태부터 시스템 전체의 상태를 확인 및 이상감지, 원인 추론까지 하는것의 개념입니다. 

##### 2번 아키텍처 설명
클라이언트(사용자)가 웹 영역에서 글 작성 완료 시, 첨부했던 이미지 파일을 Kafka로 전송합니다. 이때 이미지를 효율적으로 전달 하기 위해서 이미지를 Byte로 <b>직렬화(serialization)</b>를 진행하고 전달하도록 했습니다. 그 후 Kafka Connect를 통해 Hadoop의 HDFS로 분산 저장하는 아키텍처입니다. HDFS로 저장이 될 땐 파일을 arvo의 파일 형식으로 저장이 되고, 현재 이미지는 직렬화가 되어있어서 이미지를 활용하기 위해서는 <b>역직렬화(Deserialization)</b>를 수행해줘야 합니다.

arvo를 사용한 이유는 데이터 압축이 뛰어나다는 장점이 있습니다. 비정형 데이터인 이미지를 수집하는 환경을 만들면서 고민이 됐던 부분은 이미지의 용량이 비교적 크기 때문에 실시간으로 수집을 많이 하면 할수록 서버의 용량이 부하가 될 것이라고 예상이 됐습니다. 그래서 최대한 데이터 압축이 중요하다고 생각이 들어, 이미지를 직렬화하고 avro 형식으로 저장 해 줌으로 데이터 압축을 최대화 했습니다.
##### 3번 아키텍처 설명
웹 영역에서 로그가 수집이 되면, fluentd가 해당 경로의 로그를 읽고 Kafka로 로그를 전송하게 됩니다. kafka로 저장된 로그를 다시 fluentd가 읽어서 일정시간 주기로
OpenSearch로 저장하게 됩니다. 
## 1.0
#### 서버정보
<b>1. AWS EMR 프라이머리</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|ci-06482791TOI6QDK6CT16|m5.xlage|Spark, Hadoop, Hive 등등|
|ci-07951411XNYYV9QRZYJY|m5.xlage|Spark, Hadoop, Hive 등등|
|ci-05879379ZWNOE0BRJM7 |m5.xlage|Spark, Hadoop, Hive 등등|

<b>AWS EMR 코어</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|ci-081972235B6RIXGOXX7Z|m5.xlage|Spark, Hadoop, Hive 등등|
|ci-0649146V2TF69V78J7U|m5.xlage|Spark, Hadoop, Hive 등등|
|ci-0642542BBPKHV6KCPX|m5.xlage|Spark, Hadoop, Hive 등등|

<b>2. Kafka Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|de-kafka-cluster-1|t2.xlarge|Kafka|
|de-kafka-cluster-2|t2.xlarge|Kafka|
|de-kafka-cluster-3|t2.xlarge|Kafka|

<b>3. OpenSearch Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|de-os-manager|t2.medium|OpenSearch|
|de-os-coordinator|t2.medium|OpenSearch|
|de-os-data1|t2.medium|OpenSearch|
|de-os-data2|t2.medium|OpenSearch|
|de-os-data3|t2.medium|OpenSearch|

<b>4. 그 외</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|Prometheus Server|t2.micro|prometheus, fluentd , grafana, node_exporter|
|API Server|t2.micro|Spring, fluentd, node_exporter|
|mysql_rds|db.t3.micro|MYSQL|

#### 기술 아키텍처 및 설명
![전체 아키텍처 drawio](https://github.com/Ra99it/Study_dataengineering/assets/122541545/4972be85-5e68-4617-8e93-0e485ebae2b2)

