# Distributed-processing-and-cluster-operations-engineering

### 개요</b>
-------------------

<b> 분산 처리가 가능하고, 고 가용성을 유지 하도록 클러스터 운영을 할 수 있게 구축 한 서버 입니다.
직접 설계와 구축을 진행하면서, 다양한 기술을 적용시키는 것이 해당 프로젝트의 핵심입니다. </b>

<b>고 가용성(high availability, HA)</b> : 서버, 네트워크, 프로그램 등의 정보 시스템이 상당히 오랜 기간 동안 지속적으로 정상 운영이 가능한 성질을 말한다. 고(高)가용성이란 "가용성이 높다"는 뜻으로서, "절대 고장 나지 않음"을 의미한다.

<출처 : https://ko.wikipedia.org/wiki/%EA%B3%A0%EA%B0%80%EC%9A%A9%EC%84%B1 >
# 2.0
## 서버정보
<b>1. Hadoop and Spark with Cluster </b>
|인스턴스 이름|성능|기술|
|------|---|---|
|hadoop-master-01|m5a.xlarge|Hadoop hdfs, yarn, Spark|
|hadoop-worker-01|m5a.xlarge|Hadoop hdfs, yarn, Spark|
|hadoop-worker-02|m5a.xlarge|Hadoop hdfs, yarn, Spark|
|hadoop-worker-03|m5a.xlarge|Hadoop hdfs, yarn, Spark|

<b>2. Kafka Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|de-kafka-cluster-1|t2.xlarge |Kafka, fluentd|
|de-kafka-cluster-2|t2.xlarge |Kafka, fluentd|
|de-kafka-cluster-3|t2.xlarge |Kafka, fluentd|

<b>3. OpenSearch Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|de-os-manager|t2.medium|OpenSearch|
|de-os-coordinator|t2.medium|OpenSearch|
|de-os-data1|t2.medium|OpenSearch|
|de-os-data2|t2.medium|OpenSearch|

<b>4. 그 외</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|Prometheus Server|t2.micro|prometheus, grafana, node_exporter|
|API Server|t2.micro|Spring, node_exporter|

## 기술 아키텍처
![전체 아키텍처 drawio (3)](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/2c84d5ac-3560-488c-b6b1-8864ff1f1c66)

## 클러스터 및 서버 관계도
![클러스터 관계도 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/8d7d7e5b-10f1-4fdf-b454-7680bcefd62f)

--------------
## 설명

### 1번 아키텍처
![1번과정 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/05980476-cab7-42fc-84e7-c348304cfae1)

데이터 소스는 어떠한 형태든 Kafka로 들어오는 모든 데이터의 형태를 뜻합니다. 실습에선 Image를 사용하겠습니다.

이미지를 효율적으로 전달 하기 위해서 이미지를 Byte로 직렬화(serialization)를 진행하고 전달하도록 했습니다. 그 후 Kafka Connect를 통해 Hadoop의 HDFS로 분산 저장하는 아키텍처입니다. HDFS로 저장이 될 땐 파일을 arvo의 파일 형식으로 저장이 되고, 현재 이미지는 직렬화가 되어있어서 이미지를 활용하기 위해서는 역직렬화(Deserialization)를 수행해줘야 합니다.

arvo를 사용한 이유는 데이터 압축이 뛰어나다는 장점이 있습니다. 비정형 데이터인 이미지를 수집하는 환경을 만들면서 고민이 됐던 부분은 이미지의 용량이 비교적 크기 때문에 실시간으로 수집을 많이 하면 할수록 서버의 용량이 부하가 될 것이라고 예상이 됐습니다. 그래서 최대한 데이터 압축이 중요하다고 생각이 들어, 이미지를 직렬화하고 avro 형식으로 저장 해 줌으로 데이터 압축을 최대화 했습니다.

----------

### 2번 아키텍처
![2번아키텍처 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/6125591c-ee83-4aa9-9585-d824ad3125c7)
로그가 수집이 되면, fluentd가 해당 경로의 로그를 읽고 Kafka로 로그를 전송하게 됩니다. 그러면 kafka로 Log가 쌓이게 되면서, 일정 시간이 지나면 저장된 로그를 다시 fluentd가 읽어서 OpenSearch로 저장하는 아키텍처 입니다. OpenSearch도 클러스터로 운영하기 때문에 파티션이 나누어 지면서 보내기 때문에 보내는 과정에서 일정 로그가 누락이 될 수도 있습니다.

로그를 수집하는 이유는 무엇일까요? 만약 수집하는 환경이 구성되어 있지 않다면 시스템에 장애가 나면 로그파일에 접근 할 수 없는 상황이 생길 수 있습니다. 그리고 사용하는 프로그램이 많아지거나, 인스턴스의 수가 많아지다 보면 확인하기 어렵습니다. 해당 아키텍처는 로그를 OpenSearch로 저장하도록 했습니다. Log의 검색, 필터링, 분석 으로 활용 할 수 있기 때문에 더 신뢰성있는 시스템을 구축 할 수 있습니다.

--------

### 3번 아키텍처
![3번아키텍처 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/809e4fb6-136b-49a1-9702-bb6adcf669bf)

Api Server와 prometheus Server의 설치된 node_exporter는 서버 자원 리소스 및 네트워크 정보를 메트릭으로 수집합니다. 수집된 메트릭을 Prometheus가 HTTP 통신을 통해 메트릭을 수집하게 되고, 시각화 없이는 수집된 메트릭을 사람의 눈으로 보고 판별하기는 어렵기 때문에 Grafana로 시각화로 보여주는 아키텍처 입니다. ELK(Elastic Search, Logstash, Kibana)스택과 같은 기능을 합니다. 즉 Observability의 개념을 구현했다고 생각하시면 되겠습니다.

Observability는 기본적인 모니터링과 비슷하지만 다른 의미입니다. 모니터링은 에러나 다운타임, 비정상 상태 등을 감지하기 위해서 이벤트, 메트릭 정보를 남기고 추적하는 것을 말합니다. 반면에 Observability는 IT시스템이 복잡해지면서 HW, OS, SW, Cloud infra등 모든 데이터를 활용해서 어플리케이션의 상태부터 시스템 전체의 상태를 확인 및 이상감지, 원인 추론까지 하는것의 개념입니다.

---------------

### 버전
- <b> V2.0.0
  1. 수집된 데이터를 프로세싱을 위한 Spark 추가 및 클러스터 서버 단축운영
-------

# 1.1
## 서버정보
<b>1. Hadoop Cluster </b>
|인스턴스 이름|성능|기술|
|------|---|---|
|hadoop-master-01|m5a.xlarge|Hadoop hdfs, yarn|
|hadoop-worker-01|m5a.xlarge|Hadoop hdfs, yarn|
|hadoop-worker-02|m5a.xlarge|Hadoop hdfs, yarn|
|hadoop-worker-03|m5a.xlarge|Hadoop hdfs, yarn|

<b>2. Kafka Cluster</b>
|인스턴스 이름|성능|기술|
|------|---|---|
|de-kafka-cluster-1|t2.xlarge |Kafka, fluentd|
|de-kafka-cluster-2|t2.xlarge |Kafka, fluentd|
|de-kafka-cluster-3|t2.xlarge |Kafka, fluentd|

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
|Prometheus Server|t2.micro|prometheus, grafana, node_exporter|
|API Server|t2.micro|Spring, node_exporter|

## 기술 아키텍처
![전체 아키텍처 drawio (2)](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/14aceff9-44d8-4971-a822-fa07ced10b71)

--------------
## 설명

### 1번 아키텍처
![1번과정 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/05980476-cab7-42fc-84e7-c348304cfae1)

데이터 소스는 어떠한 형태든 Kafka로 들어오는 모든 데이터의 형태를 뜻합니다. 실습에선 Image를 사용하겠습니다.

이미지를 효율적으로 전달 하기 위해서 이미지를 Byte로 직렬화(serialization)를 진행하고 전달하도록 했습니다. 그 후 Kafka Connect를 통해 Hadoop의 HDFS로 분산 저장하는 아키텍처입니다. HDFS로 저장이 될 땐 파일을 arvo의 파일 형식으로 저장이 되고, 현재 이미지는 직렬화가 되어있어서 이미지를 활용하기 위해서는 역직렬화(Deserialization)를 수행해줘야 합니다.

arvo를 사용한 이유는 데이터 압축이 뛰어나다는 장점이 있습니다. 비정형 데이터인 이미지를 수집하는 환경을 만들면서 고민이 됐던 부분은 이미지의 용량이 비교적 크기 때문에 실시간으로 수집을 많이 하면 할수록 서버의 용량이 부하가 될 것이라고 예상이 됐습니다. 그래서 최대한 데이터 압축이 중요하다고 생각이 들어, 이미지를 직렬화하고 avro 형식으로 저장 해 줌으로 데이터 압축을 최대화 했습니다.

### 1번 아키텍처 구현화면

----------

### 2번 아키텍처
![2번아키텍처 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/6125591c-ee83-4aa9-9585-d824ad3125c7)
로그가 수집이 되면, fluentd가 해당 경로의 로그를 읽고 Kafka로 로그를 전송하게 됩니다. 그러면 kafka로 Log가 쌓이게 되면서, 일정 시간이 지나면 저장된 로그를 다시 fluentd가 읽어서 OpenSearch로 저장하는 아키텍처 입니다. OpenSearch도 클러스터로 운영하기 때문에 파티션이 나누어 지면서 보내기 때문에 보내는 과정에서 일정 로그가 누락이 될 수도 있습니다.

로그를 수집하는 이유는 무엇일까요? 만약 수집하는 환경이 구성되어 있지 않다면 시스템에 장애가 나면 로그파일에 접근 할 수 없는 상황이 생길 수 있습니다. 그리고 사용하는 프로그램이 많아지거나, 인스턴스의 수가 많아지다 보면 확인하기 어렵습니다. 해당 아키텍처는 로그를 OpenSearch로 저장하도록 했습니다. Log의 검색, 필터링, 분석 으로 활용 할 수 있기 때문에 더 신뢰성있는 시스템을 구축 할 수 있습니다.

### 2번 아키텍처 구현화면

--------

### 3번 아키텍처
![3번아키텍처 drawio](https://github.com/Ra99it/Distributed-processing-and-cluster-operations-engineering/assets/122541545/809e4fb6-136b-49a1-9702-bb6adcf669bf)

Api Server와 prometheus Server의 설치된 node_exporter는 서버 자원 리소스 및 네트워크 정보를 메트릭으로 수집합니다. 수집된 메트릭을 Prometheus가 HTTP 통신을 통해 메트릭을 수집하게 되고, 시각화 없이는 수집된 메트릭을 사람의 눈으로 보고 판별하기는 어렵기 때문에 Grafana로 시각화로 보여주는 아키텍처 입니다. ELK(Elastic Search, Logstash, Kibana)스택과 같은 기능을 합니다. 즉 Observability의 개념을 구현했다고 생각하시면 되겠습니다.

Observability는 기본적인 모니터링과 비슷하지만 다른 의미입니다. 모니터링은 에러나 다운타임, 비정상 상태 등을 감지하기 위해서 이벤트, 메트릭 정보를 남기고 추적하는 것을 말합니다. 반면에 Observability는 IT시스템이 복잡해지면서 HW, OS, SW, Cloud infra등 모든 데이터를 활용해서 어플리케이션의 상태부터 시스템 전체의 상태를 확인 및 이상감지, 원인 추론까지 하는것의 개념입니다.

### 3번 아키텍처 구현화면
---------------

### 버전
- <b> V1.1.0
  1. 전체적인 아키텍처 수정
  2. 원활한 실습과 데이터 엔지니어링 부분 집중을 위해 Hadoop Cluster 축소 </b>
- <b> V1.1.1
  1. Kafka 와 HDFS를 연결해주는 Kafka Connect를 Standalone Mode(단일 모드)에서 Distributed Mode(분산 모드) 변경하여 고 가용성을 높혔습니다. 
  2. fluentd도 분산적으로 처리를 해 많은 양의 데이터가 들어와도 단일적으로 처리하는 것 보다 효율적으로 구현했습니다. (1.0에선 하나의 서버로 구동했습니다.)</b>
-------
# 1.0
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

#### 기술 아키텍처 
![전체 아키텍처 drawio](https://github.com/Ra99it/Study_dataengineering/assets/122541545/4972be85-5e68-4617-8e93-0e485ebae2b2)

#### 1번 아키텍처 설명
Api Server와 prometheus Server의 설치된 node_exporter는 서버 자원 리소스 및 네트워크 정보를 메트릭으로 수집합니다. 수집된 메트릭을 Prometheus가 HTTP 통신을 통해 메트릭을 수집하게 되고, 시각화 없이는 수집된 메트릭을 사람의 눈으로 보고 판별하기는 어렵기 때문에 Grafana로 시각화로 보여주는 아키텍처 입니다. ELK(Elastic Search, Logstash, Kibana)스택과 같은 기능을 합니다. 즉 Observability의 개념을 구현했다고 생각하시면 되겠습니다.

Observability는 기본적인 모니터링과 비슷하지만 다른 의미입니다. 모니터링은 에러나 다운타임, 비정상 상태 등을 감지하기 위해서 이벤트, 메트릭 정보를 남기고 추적하는 것을 말합니다. 반면에 Observability는  IT시스템이 복잡해지면서 HW, OS, SW, Cloud infra등 모든 데이터를 활용해서 어플리케이션의 상태부터 시스템 전체의 상태를 확인 및 이상감지, 원인 추론까지 하는것의 개념입니다. 

#### 2번 아키텍처 설명
클라이언트(사용자)가 웹 영역에서 글 작성 완료 시, 첨부했던 이미지 파일을 Kafka로 전송합니다. 이때 이미지를 효율적으로 전달 하기 위해서 이미지를 Byte로 <b>직렬화(serialization)</b>를 진행하고 전달하도록 했습니다. 그 후 Kafka Connect를 통해 Hadoop의 HDFS로 분산 저장하는 아키텍처입니다. HDFS로 저장이 될 땐 파일을 arvo의 파일 형식으로 저장이 되고, 현재 이미지는 직렬화가 되어있어서 이미지를 활용하기 위해서는 <b>역직렬화(Deserialization)</b>를 수행해줘야 합니다.

arvo를 사용한 이유는 데이터 압축이 뛰어나다는 장점이 있습니다. 비정형 데이터인 이미지를 수집하는 환경을 만들면서 고민이 됐던 부분은 이미지의 용량이 비교적 크기 때문에 실시간으로 수집을 많이 하면 할수록 서버의 용량이 부하가 될 것이라고 예상이 됐습니다. 그래서 최대한 데이터 압축이 중요하다고 생각이 들어, 이미지를 직렬화하고 avro 형식으로 저장 해 줌으로 데이터 압축을 최대화 했습니다.
#### 3번 아키텍처 설명
로그가 수집이 되면, fluentd가 해당 경로의 로그를 읽고 Kafka로 로그를 전송하게 됩니다. 그러면 kafka로 Log가 쌓이게 되면서, 일정 시간이 지나면 저장된 로그를 다시 fluentd가 읽어서 OpenSearch로 저장하는 아키텍처 입니다. OpenSearch도 클러스터로 운영하기 때문에 파티션이 나누어 지면서 보내기 때문에 보내는 과정에서 일정 로그가 누락이 될 수도 있습니다.

로그를 수집하는 이유는 무엇일까요? 만약 수집하는 환경이 구성되어 있지 않다면 시스템에 장애가 나면 로그파일에 접근 할 수 없는 상황이 생길 수 있습니다. 그리고 사용하는 프로그램이 많아지거나, 인스턴스의 수가 많아지다 보면 확인하기 어렵습니다. 해당 아키텍처는 로그를 OpenSearch로 저장하도록 했습니다. Log의 검색, 필터링, 분석 으로 활용 할 수 있기 때문에 더 신뢰성있는 시스템을 구축 할 수 있습니다.

##### 버전

+ <b>1.0.0 : 초기 </b>   
+ <b>1.0.1 : ip 노출 최소화, port 관리 , 보안 위주</b>   

