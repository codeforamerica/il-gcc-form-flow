# Online Application for the Illinois Child Care Assistance Program (CCAP)

- [Online Application for the Illinois Child Care Assistance Program (CCAP)](#online-application-for-the-illinois-child-care-assistance-program-ccap)
- [About GetChildCareIL](#about-getchildcareil)
  - [Product](#product)
  - [Technology](#technology)
- [I. Local development set up instructions](#i-local-development-set-up-instructions)
  - [Git](#git)
  - [Environment variables](#environment-variables)
  - [Dev set up: script for Mac and Linux](#dev-set-up-script-for-mac-and-linux)
  - [Dev set up: Windows (manual)](#dev-set-up-windows-manual)
    - [Install dependencies](#install-dependencies)
    - [Create databases](#create-databases)
    - [Run tests](#run-tests)
    - [Create cert for https on localhost](#create-cert-for-https-on-localhost)
  - [Running the app for the first time](#running-the-app-for-the-first-time)
  - [Seeding your local database](#seeding-your-local-database)
  - [Set up Live Reload](#set-up-live-reload)
- [II. Development](#ii-development)
  - [Tests](#tests)
  - [Scripts](#scripts)
    - [- setup.sh](#--setupsh)
    - [- generate\_localhost\_cert.sh](#--generate_localhost_certsh)
    - [- generate\_migration.sh](#--generate_migrationsh)
  - [Troubleshooting IntelliJ](#troubleshooting-intellij)
    - [Application Won't Run After IntelliJ Update](#application-wont-run-after-intellij-update)
  - [Contributing Live Templates to Your App](#contributing-live-templates-to-your-app)
    - [If you want to use a local version of the Form-Flow Library (for library development)](#if-you-want-to-use-a-local-version-of-the-form-flow-library-for-library-development)
  - [Actuator Endpoints](#actuator-endpoints)
- [III. Cloud infrastructure](#iii-cloud-infrastructure)
  - [Option 1. AWS set up](#option-1-aws-set-up)
  - [Option 2. Aptible + AWS set up](#option-2-aptible--aws-set-up)
    - [Aptible Setup](#aptible-setup)
    - [AWS S3 document storage](#aws-s3-document-storage)
    - [Deployment in Aptible to a Custom URL](#deployment-in-aptible-to-a-custom-url)
      - [Route53 Setup](#route53-setup)
      - [Aptible Endpoint Setup](#aptible-endpoint-setup)
      - [Request Public Certificate](#request-public-certificate)
      - [S3 Static Hosting for Redirect Requests (non-www traffic -\> www)](#s3-static-hosting-for-redirect-requests-non-www-traffic---www)
      - [Cloudfront Distribution Setup](#cloudfront-distribution-setup)

# About GetChildCareIL

## Product

Get Child Care IL (abbreviated as IL-GCC) allows families in Illinois to apply online for the Child Care Assistance Program (CCAP). CCAP is a benefit program funded by the Illinois Department of Human Services (IDHS) that helps cover the cost of child care, with eligibility based on income level and family size. 

When a family is approved for CCAP, IDHS pays their child care provider(s) directly, to subsidize some or all of the cost of their children’s care. Examples of CCAP providers include daycare centers, afterschool programs, or care from a friend or family member.

Families apply through the site by answering questions and uploading supporting documents. Once a family has submitted their application, they are prompted to notify their chosen child care provider(s). The provider(s) can use the app to reply to the family's request for care, fill out their respective info, and register as a new CCAP provider if applicable. The provider's information is added to the family's application, after which the combined application (containing both the family and provider data) along with all submitted supporting documents are transmitted to the State of IL's Child Care Management System (CCMS) via an API request. 

From there, the application will assigned to a dedicated Child Care Referral and Resource (CCR&R) agency for processing and eligibility determination.

## Technology

This application is a Java Spring Boot application built on Code for America's open source [Form Flow Library (`form-flow`)](https://github.com/codeforamerica/form-flow). It utilizes common, frequently-used libraries throughout, most of which are free and open source.

- `form-flow` is included in the application's `build.gradle`, along with all other dependencies. A detailed explanation of `form-flow` concepts and underlying functionality can be found in the [library's README](https://github.com/codeforamerica/form-flow)
- [JobRunr](https://www.jobrunr.io/en/documentation/) is used in this application to manage background jobs (eg, scheduling of email notifications, submission of applications over https)

In addition, the following product capabilities are enabled using third-party services:
- Street address validation using [Smarty](https://www.smarty.com/docs/cloud/us-street-api)
- Email notifications using [Twilio Sendgrid ](https://www.twilio.com/docs/sendgrid/for-developers/sending-email/email-quickstart-for-java)
- Email address validation using  [Twilio SendGrid Real Time Email Validation](https://www.twilio.com/docs/sendgrid/ui/managing-contacts/email-address-validation)
- User analytics and event tracking using [Mixpanel](https://docs.mixpanel.com/docs/what-is-mixpanel)
- Error analytics and tracing using [Sentry](https://docs.sentry.io/platforms/java/)
- Observability, logging, monitoring, alerting using [Datadog](https://docs.datadoghq.com/)


# I. Local development set up instructions

## Git

1. First, ensure that you have [git](https://git-scm.com/) installed on your local machine. If desired, you can also install a [Github desktop client](https://github.com/apps/desktop) or preferred git plugin for your IDE.
2. Clone this repository into your local environment. From your command line: 
 ```bash
  git clone git@github.com:codeforamerica/il-gcc-form-flow.git
  ```

## Environment variables
You'll need to provide some environment variables to run this application locally. 
1. To do this, create a new `.env` file by copying the `sample.env` file in the root directory of your project:
```
cp sample.env .env
```
1. You can find the dev environment variables and configs in the `Shared-SNLAB-IL` folder in LastPass. Put those values into your `.env` file.
2. If you use [IntelliJ IDEA](https://www.jetbrains.com/idea/), download the [EnvFile plugin](https://plugins.jetbrains.com/plugin/7861-envfile) and follow the setup instructions [here](https://github.com/Ashald/EnvFile#usage) to set up Run Configurations with EnvFile. (see more IDE setup instructions below)

## Dev set up: script for Mac and Linux

The `setup.sh` script:
- uses [Homebrew](https://brew.sh/) to install all the necessary dependencies (such as java, gradle, postgres, node)
- runs the `generate_localhost_cert.sh` script, which installs dependencies to create an SSL certification so you can use `https` with `localhost` during development
- creates the main app database and users needed to run the application locally
- creates the database needed for running tests, and then runs the tests

**To run from the command line,** go to the root of the repository and run the `setup.sh` script:
```
./scripts/setup.sh
```

**Or, run from within IntelliJ** by clicking on the `setup.sh` file in the `scripts` directory and select `Run 'setup.sh'`.

## Dev set up: Windows (manual)

### Install dependencies
1. Install [Java Temurin v 21](https://adoptium.net/temurin/releases/?os=any&arch=any&version=21)
   - Set 21 as your Java version in JAVA_HOME - [see docs](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html)
1. Install [Gradle v8.11 or higher](https://gradle.org/install/#manually)
1. Install [Postgres v14 or higher](https://www.postgresql.org/download/windows/)
1. Install [Libreoffice](https://www.libreoffice.org/get-help/install-howto/windows/)

### Create databases
1. Make sure postgres is running
1. Run the sql commands from the setup.sh script

### Run tests

```
./gradlew clean test
```

### Create cert for https on localhost
```
./generate_localhost_cert.sh
```


## Running the app for the first time

1. If you need to set up the IntelliJ IDE, start by following the instructions from the [form flow library here.](https://github.com/codeforamerica/form-flow#intellij-setup)
1. In IntelliJ, run the application using the `IlGCCApplication` configuration (found in `org.ilgcc.app`). The run  will fail, but it will create a new run configuration in IntelliJ. 
1. Open this new run configuration and configure it to use the `.env` file you created earlier with the EnvFile plugin:
   - Run -> Edit Configurations -> IlGCCApplication. 
   - Select `Enable EnvFile` and manually add the `.env` file you created using the `+` button.
1. In your `.env` file, make sure the `SPRING_PROFILES_ACTIVE` is set to `dev`
1. Run the application again. The application should start up successfully.

## Seeding your local database

The following steps will allow you to seed your local db with the necessary counties, providers, and resource organizations:

- Import zip code <> county mapping data
- Import resource organization data
- Import provider data


## Set up Live Reload

Live Reload is very helpful when making changes to HTML templates, CSS, or JavaScript. Here are instructions on how to get IntelliJ to reload resources and have the LiveReload browser extension automatically reload the browser tab for you.

1. Download live reload extension in your browser of choice:
    * [Firefox extension](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/?utm_source=addons.mozilla.org&utm_medium=referral&utm_content=search)
    * [Chrome extension - works with Chrome, Edge, Brave, Arc](https://chromewebstore.google.com/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)
1. Restart your browser after install
1. In IntelliJ, go to Edit configuration -> Modify options
        * On Frame deactivation:
            * Check - Update classes and resources
1. Run your application from IntelliJ
1. In your browser, go to `http://localhost:8080/`
1. Check that the live reload extension is "turned on", it will either be a solid color or a filled dot in the middle of the icon
1. Now when you move focus away from IntelliJ, it will trigger an update and will then trigger a browser refresh

> 📹 Here's a [video going step by step through these instructions](https://www.loom.com/share/74183c76d45c416e870ccf7aa06dd8ee?sid=a3bf01f9-b22d-423a-b61c-050bb0620d02).


# II. Development


## Tests

This application includes the following types of automated tests:
- **Unit tests** ([Junit](https://junit.org/junit5/))
- **Integration / journey tests** ([Selenium](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/package-summary.html))
- **Accessibility tests**: selenium tests using [axe-core library](https://github.com/dequelabs/axe-core) to test against [WCAG 2.1 rules](https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md) as well as [Deque accessibility best practices](https://github.com/dequelabs/axe-core/blob/develop/doc/rule-descriptions.md#best-practices-rules)

**To compile and run all tests:**

```
./gradlew clean test
```

**To run accessibility tests:**
```
./gradlew accessibilityTest
```

**To run a single journey test:**

```
./gradlew test --tests org.ilgcc.app.journeys.GccFlowJourneyTest.fullGccFlow
```


## Scripts

In the `scripts` directory, you can find the following useful scripts:

### - setup.sh
(see above)

### - generate_localhost_cert.sh
(see above)
### - generate_migration.sh

This script will generate an empty migration file and place it in
the following directory: `src/main/resources/db/migration`. If you'd like
to change this location, you can edit the `generate_migration.sh` file
and update the `migrations_paths` variable.

The script will create the migration directory if it does not already exist.

It will prompt you to enter a short description of what the migration will do.
It will then generate the file, with a name based on that description. The script
will then open up the migration file in IntelliJ for you.

To run the script, simply type:

```bash
 ./generate_migration.sh
```

_Note: if it does not run on the command line, you may need to give the script executable permission first, by running `chmod u+x generate_migration.sh`_


## Troubleshooting IntelliJ
### Application Won't Run After IntelliJ Update

Sometimes an IntelliJ update will cause the `ILGCCApplication` run context to fail. Here are some
ways to attempt to fix it.

1. Invalidate the cache
    * File -> Invalidate Caches...
    * This will invalidate the caches and restart IntelliJ.
    * Afterward, try to run the application. If this issue isn't fixed, try suggestion 2.
2. Remove `.idea/modules`
    * In the root of the repository, look for `.idea/modules`
    * Make a copy of this folder and save somewhere else
    * Delete this folder and all of its contents
    * Quit IntelliJ
    * Open IntelliJ, rebuild the project, hopefully modules are re-created from the application
      context
3. If both of the above fail
    * Make a copy of the entire `.idea` folder and save somewhere else.
    * Delete the original `.idea` folder
    * Quit IntelliJ
    * Open IntelliJ, rebuild the project, hopefully modules are re-created from the application
      context and found

## Contributing Live Templates to Your App

If you have created live templates with fragments which are specific to your application based on a starter app template, you can commit them to your repository. You will follow a similar pattern to create templates to what is outlined [in the form-flow library here.](https://github.com/codeforamerica/form-flow#contribute-new-live-templates)

An example template which was set up using this process, starting from an html snippet is available [in this repository's IntelliJ settings folder](intellij-settings/StarterAppLiveTemplate.xml).

### If you want to use a local version of the Form-Flow Library (for library development)

To use a local version of the  [form-flow](https://github.com/codeforamerica/form-flow), library you can do the following:

1. Clone the `form-flow` repo in the same directory as the starter app.
2. Build the `form-flow` library jar.
3. In this starter app, set the `SPRING_PROFILES_ACTIVE`  to `dev` in
   the [`.env`](https://github.com/codeforamerica/form-flow-starter-app/blob/main/sample.env) file.
4. Start the `form-flow-starter-app`.

Changing the `SPRING_PROFILES_ACTIVE` to `dev` will cause the starter
app's [build.gradle](build.gradle) to pull in the local library, via this line:

 ```
 implementation fileTree(dir: "$rootDir/../form-flow/lib/build/libs", include: '*.jar')
 ```

## Actuator Endpoints

The [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) is a feature Spring Boot provides to monitor and interact with your application. It opens endpoints that can be queried to get information about your application, like health and build information.

This app, by default, enables full access to all actuator endpoints in the `dev` profile for local development. However, in other profiles, it restricts access, allowing only the health and build information endpoints to be available for use outside of local development.

** ⚠️ This feature can be a large security concern if certain endpoints are open. ⚠️**

Please read the following section in  [Form Flow Library's documentation](https://github.com/codeforamerica/form-flow#actuator-endpoints)
as well as Spring Boot's [documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html).
It's extremely important that you understand the actuator and what it provides and the risks, so that you can configure the feature accordingly.


# III. Cloud infrastructure
## Option 1. AWS set up
To run set up this application to run exclusively on AWS, please see https://github.com/codeforamerica/il-gcc-infra


## Option 2. Aptible + AWS set up

### Aptible Setup

For [Aptible](https://www.aptible.com/) configuration information, please see
their [documentation](https://deploy-docs.aptible.com/).

The [Aptible CLI documentation](https://deploy-docs.aptible.com/docs/cli) is particularly helpful.

Here are the general steps to setup a new application in Aptible:

1. Create a new environment and application in Aptible, or create a new application in an existing
   environment.
2. Setup Aptible permissions to enable deploying your application, if they do not already exist.
3. Provision a database for your application in Aptible.
4. Add repository secrets for the deploy github action.

### AWS S3 document storage

1. Provision a new AWS S3 bucket.
    1. We use two buckets: one for non-production purposes and another for production.
2. Replace the bucket names with your newly created buckets in
   the [main application configuration](src/main/resources/application.yaml) and
   the [demo application configuration](src/main/resources/application-demo.yaml).

### Deployment in Aptible to a Custom URL

These instructions guide you through the process of creating resources in Aptible + AWS which will
allow you to deploy your application to a custom domain with root domain forwarding. Please create
the resources in the specified order, as there are
dependencies:

#### Route53 Setup

1. Create a new hosted zone with the name corresponding to the root domain of your purchased domain
   name.

#### Aptible Endpoint Setup

1. Create a new managed HTTPS endpoint for your root domain with subdomain (i.e. www)
2. In Aptible, be sure to include the following variable in your application's configuration environment: [`FORCE_SSL=true`](https://www.aptible.com/docs/https-redirect).
3. Follow the instructions to create managed HTTPS validation records in Route53

#### Request Public Certificate

1. Request certificate in AWS Certificate Manager (ACM) for your purchased domain name. If you would
   like to support directing non-www to www traffic, please use your root domain for the fully
   qualified domain name in the request.
2. Create records in AWS Route53

#### S3 Static Hosting for Redirect Requests (non-www traffic -> www)

1. Create a new S3 bucket with your root domain.
2. Under the bucket properties, configure static website hosting with hosting type
   of `Redirect requests for an object`. Select Protocol of `none`.

#### Cloudfront Distribution Setup

1. Create a new CloudFront distribution with CNAME corresponding to your root domain
2. Associate the certificate that you created for your root domain. All other settings can remain as
   defaults.
3. Create a Route53 Alias record for the root domain which points to your cloudfront distribution.

