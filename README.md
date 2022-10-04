# com.wso2.session.termination.handler

A custom event handler built to terminate active user sessions when there is a change in user roles.

## Prepare

### Prerequisites for Building

The following prerequisites are required to build this project.

- Java
- Maven

To check whether your environment adheres to these you can execute the `prerequisites.sh` file with the
command `sh prerequisites.sh`. Something similar to the following will be shown at a successful execution.

```
openjdk version "1.8.0_302"
OpenJDK Runtime Environment (build 1.8.0_302-b08)
OpenJDK 64-Bit Server VM (build 25.302-b08, mixed mode)
Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /xxx/xxx/.sdkman/candidates/maven/current
Java version: 1.8.0_302, vendor: Oracle Corporation, runtime: /xxx/xxx/.sdkman/candidates/java/8.0.302-open/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.15.0-41-generic", arch: "amd64", family: "unix"
```

### WSO2 Identity Server

Find and open the `deployment.toml` file located at `<IS_HOME>/repository/conf/` and append the following lines to
register the event handler, and it's subscriptions.

```
[[event_handler]]
name= "sessionTermination"
subscriptions = ["POST_UPDATE_USER_LIST_OF_ROLE", "POST_UPDATE_ROLE_LIST_OF_USER"]
```

### Clone and Build

Clone and build the project by executing the following commands sequentially:

```
git clone https://github.com/deshankoswatte/identity-event-handler-session-termination.git
mvn clean install
```

### Deploy

After successfully building the project, copy the artifacts
`com.wso2.session.termination.handler-1.0.0.jar` and `com.wso2.common-1.0.0-SNAPSHOT.jar` from the target folder and
paste it inside the `<IS HOME>/repository/components/dropins` folder.

## Run

Start your WSO2 Identity Server by executing the command `sh wso2server.sh` from your `<IS HOME>/bin` folder.

## Test

### Scenario Reproduction Steps

1. Create a user and a role.
2. Assign the created role to the user.
3. Create service providers for sample applications such as `pickup-dispatch` and `pickup-manager`.
4. Deploy the sample applications.
5. Single Sign-On into the sample applications.
6. Remove the assigned role of the user through the WSO2 Management Console.
7. The active sessions of that user will be revoked, and he/she will be logged out of the sample applications.

### Tested Environment Details

```
Operating System - Ubuntu 20.04
Java Version - 1.8
Identity Server Versions - IS-5.10.0, IS-5.11.0 (Logout doesn't work since back-channel logut was not available at the time of repo creation)
Tomcat Version - 9.0.50
```

### More Information

For more information, please refer to
the [Medium blog](https://deshankoswatte.medium.com/terminate-active-user-sessions-on-user-role-change-events-through-the-wso2-identity-sever-2462cf46eff8)
which describes the entre process in detail.
