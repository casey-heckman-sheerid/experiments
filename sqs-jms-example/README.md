# SQS JMS Example

This application demonstrates the use of the AWS SQS JMS library to read messages from an SQS queue.

It will send messages to the configured topic and read messages from the configured queue.

## Prerequisites

* Java 1.8 or later
* Maven 3.5 or later
* AWS Account with privileges to create SNS Topics and SQS Queues

## Configuration

* AWS:
  * Create an SNS Topic
  * Create an SQS Queue and subscribe it to the topic
* Edit the `config/application.properties` file, and change the values there for your Topic and Queue.

## Running the Application

`run.sh` will rebuild the project and start the application.

Use the `--profile` option to specify an AWS profile other than `default`.

## Debugging the Application

Run the application as described above with the `--debug` option.  

The application will pause during startup and wait for you to attach the debugger.

To attach a debugger using IntelliJ:

1. Create a new configuration (if you haven't already -- you only need to do this once):
   * Run > Edit Configurations...
   * Click the '+' and choose `Remote`
   * Name the configuration, e.g. `Application Debug`
   * Command line arguments: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`
   * Host: `localhost` Port: `5005`
   * Press OK.
1. Choose that configuration from the Run menu, and press the debug button to attach the debugger.
