# Kafka based distance tracker

Calculate total distance and time of the route in the provided GPX file in real time


1. creates producer which sends log from the embedded csv GPX file to Kafka topic periodically
2. creates kafka DSL which calculates and displays distance and time in real time in console

## General idea of the solution

1. Stream events to Kafka topic periodically. 
The key will be a random UUID every time the app runs, the value will be a point info in JSON
2. Two consumers from separate consumer groups will read the same topic
3. Total distance consumer should:
⋅⋅* group by key
⋅⋅* aggregate total distance for every event
⋅⋅* print the result for every event 
4. Windowed distance consumer should:
⋅⋅* group by key
⋅⋅* aggregate total distance every minute with 0 grace 
⋅⋅* suppress printing until the window closes
⋅⋅* print the result for every event 

## Installation

Clone the project from git repository

## Usage

Just run the jar file for now. Soon will be docker-compose.

## Prerequisites

JDK 8/gradle/kafka

Go to the cloned repo folder
```bash
# Build the project
.gradlew build
# Change dir
cd build/libs/
# Run the fat jar
java -jar route-distance-1.0-SNAPSHOT-all.jar
```

## How the app works

### Csv reader

There is a csv stream reader that will load the csv file with points and return them

### Point processor

Point processor will create Kafka producer and send the points as events. 
In order to identify the time interval to imitate real-time streaming there is an interval strategy

#### Interval strategy

Default interval strategy is calculating total time difference in seconds between first and last 
point and will calculate the average based on total amount of points. For the example csv it will be 7 seconds.  

The point processor will create a single threaded scheduler using the specified interval and will run the job
which will be a producer event sending. The strategy within a period of time is 
to measure how many events per the period we can handle. For that we check how much time was left since last event and decide 
if it's time to process it. 

### Producer
Kafka producer will be sending events into **gpx-points** topic

### Distance calculation

Using ```io.genetix``` library ```Geoid.WGS84.distance``` method. The result is slightly different from the result csv though.

### Consumers

#### Windowed total distance consumer 

is a kafka stream that consumes  **gpx-points** topic in a separate group id.

##### Purpose

Print total distance per one minute window and then reset the total distance until next minute is passed.

Message format: ```Total distance after 1 minute (M): <distance with 8 digit precision>```

#### Total distance consumer

Total distance consumer is a kafka stream that consumes  **gpx-points** topic in a separate group id.

##### Purpose
Print total distance per one event.

Message format: ```| <event time in yyyy.MM.dd H:mm:ss> | <distance with 3 digit precision>|"```

## Example output

For total distances

```| 2012.12.03 9:08:42 | 0.000 |
| 2012.12.03 9:08:43 | 0.015 |
| 2012.12.03 9:08:44 | 0.105 |
| 2012.12.03 9:08:58 | 2.457 |
| 2012.12.03 9:09:08 | 4.487 |
| 2012.12.03 9:09:18 | 6.494 |
| 2012.12.03 9:09:24 | 9.456 |
| 2012.12.03 9:09:29 | 11.347 |
| 2012.12.03 9:09:38 | 12.120 |
| 2012.12.03 9:09:39 | 12.366 |
| 2012.12.03 9:10:32 | 16.216 |
```

### Compared to the canonical in the excel file:

| Calculated by app | Canonical           |
| ------------- |:-------------:| 
| 0.000      | 0 | 
| 0.015     | 0      |
| 0.105 | 0,095      |
| 2.457 | 2,446      |
| 4.487 | 4,471      |
| 6.494 | 6,476      |
| 9.456 | 9,432      |
| 11.347 | 11,318      |
| 12.120 | 12,090      |
| 12.366 | 12,342      |
| 16.216 | 16,184      | 

## Problems:

I still can't figure out how to correctly calculate total distance per minute and sometimes 
the windowed consumer doesn't behave correctly, printing the distance ahead of time.