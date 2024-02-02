# Distributed-processing-and-cluster-operations-engineering


### 개요</b>
-------------------

<b> 분산 처리가 가능하고, 고 가용성을 유지 하도록 클러스터 운영을 할 수 있게 구축 해 본 서버 입니다.
직접 설계와 구축을 진행하면서, 다양한 기술을 적용시키는 것이 주요 목적입니다. </b>


<b>고 가용성(high availability, HA)</b> : 서버, 네트워크, 프로그램 등의 정보 시스템이 상당히 오랜 기간 동안 지속적으로 정상 운영이 가능한 성질을 말한다. 고(高)가용성이란 "가용성이 높다"는 뜻으로서, "절대 고장 나지 않음"을 의미한다.

<출처 : https://ko.wikipedia.org/wiki/%EA%B3%A0%EA%B0%80%EC%9A%A9%EC%84%B1 >

### 기술 아키텍처(v1.0.0) 
-------------------
![전체 아키텍처 drawio](https://github.com/Ra99it/Study_dataengineering/assets/122541545/4972be85-5e68-4617-8e93-0e485ebae2b2)

##### 1번 아키텍처 설명


##### 2번 아키텍처 설명
클라이언트(사용자)가 웹 영역에서 글 작성 완료 시, 첨부했던 이미지 파일을 Kafka로 전송합니다. 이때 이미지의 용량을 효율적으로 전달 하기 위해서 이미지를 배열에서 Byte 화로 직렬화하고 전달하도록 했습니다. 그 후 Kafka Connect를 통해 Hadoop의 HDFS로 저장하는 아키텍처입니다. HDFS로 저장이 될 땐 파일을 arvo의 파일 형식으로 저장이 되고, 현재 이미지는 Byte로 되어있기 때문에 역 직렬화를 진행해 줘야 합니다.

arvo를 사용한 이유는 데이터 압축이 뛰어나다는 장점이 있습니다. 비정형 데이터인 이미지를 수집하는 환경을 만들면서 고민이 됐던 부분은 이미지의 용량이 비교적 크기 때문에 수집을 많이 하면 할수록 서버의 용량이 부하가 될 것이라고 예상이 됐습니다. 그래서 최대한 데이터 압축이 중요하다고 생각해 고안하게 되었습니다.
##### 3번 아키텍처 설명

## 1.0.0
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

