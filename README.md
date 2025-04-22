# IL GCC Application

Table of Contents
=================

* [What is the IL GCC App](#what-is-the-il-gcc-app)
* [Setup Instructions](#setup-instructions)
    * [Mac and Linux](#mac-and-linux)
    * [Windows](#windows)
    * [Setup Environment](#setup-environment)
        * [IntelliJ](#intellij) 
        * [Setup Application](#setup-application)
        * [Contributing Live Templates to Your App](#contributing-live-templates-to-your-app)
        * [Using a Local Version of the Form-Flow Library (For Form-Flow Library Developers)](#using-a-local-version-of-the-form-flow-library-for-form-flow-library-developers)
* [Using this as a template repository](#using-this-as-a-template-repository)
    * [Actuator Endpoints](#actuator-endpoints)
    * [Scripts](#scripts)
        * [setup.sh](#setupsh) 
        * [generate_migration.sh](#generatemigrationsh)
    * [AWS Setup](#aws-setup)
    * [Cloud Deployment](#cloud-deployment)
        * [Aptible Setup](#aptible-setup)
        * [Deployment in Aptible to a custom URL](#deployment-in-aptible-to-a-custom-url)
    * [Development Setup](#development-setup)
* [Troubleshooting IntelliJ](#troubleshooting-intellij)

# What is the IL GCC App

The IL GCC Application (aka Illinois Get Childcare App) is a Spring Boot application built on the `form-flow` Java library. It
It's a plain, straightforward (but modern) Spring app that uses common, frequently-used libraries throughout. 

The IL GCC Application has been built to allow residents of Illinois to apply online for child care benefits, and 
to allow child care providers to respond online to these applications.

It contains example code for a simple, generic application for public benefits. An applicant
can fill out screens with their basic info, upload supporting documents, then submit it all.
Upon submission, they receive an email with a filled-in
application PDF. The entire experience is in both English and Spanish.

The `form-flow` Java library is included in the application's `build.gradle` along with all other 
dependencies. The codebase for the `form-flow` library is [open source](https://github.com/codeforamerica/form-flow).

A detailed explanation of form flow concepts can be found in
the [form flow library's readme](https://github.com/codeforamerica/form-flow).

# Setup instructions

## Mac and Linux

Start by cloning this repository. After cloning the repository, run `setup.sh`. In IntelliJ
you can right click on the `setup.sh` file in the scripts directory and select `Run 'setup.sh'`.
This will install all the necessary dependencies and create the databases and users needed for the 
application.

## Windows

Check the script `scripts/setup.sh` for the most up-to-date list of dependencies and steps you'll 
need to install manually.

## Setup Environment

Note that you'll need to provide some environment variables specified in a `.env` file to run this 
application. We use IntelliJ and have provided setup instructions for convenience.

We have provided a `sample.env` file in the root directory which you can copy for your convenience.

Create a new `.env` by copying the `sample.env` file in the root directory of your project. 
You can run `cp sample.env .env` to do so. You can find the necessary environment variables in the 
`Shared-SNLAB-IL` folder in LastPass which you can fill the blanks in with.

### IntelliJ

- Download the [EnvFile plugin](https://plugins.jetbrains.com/plugin/7861-envfile) and follow the
  setup instructions[here](https://github.com/Ashald/EnvFile#usage) to set up Run Configurations with
  EnvFile.

#### Setup Live Reload

Live Reload is very helpful when making many changes to HTML templates, CSS, or JavaScript. Here are instructions on how to get IntelliJ to reload resources and have the LiveReload browser extension automatically reload the browser tab for you.

* Download live reload extension in your browser of choice:
    * [Firefox extension](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/?utm_source=addons.mozilla.org&utm_medium=referral&utm_content=search)
    * [Chrome extension - works with Chrome, Edge, Brave, Arc](https://chromewebstore.google.com/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)
* Restart your browser after install
* In IntelliJ, go to Edit configuration
    * Modify options
        * On Frame deactivation:
            * Check - Update classes and resources
* Run your application from IntelliJ
* Go to `http://localhost:8080/`
* Check that the live reload extension is "turned on", it will either be a solid color or a filled dot in the middle of the icon
* Now when you move focus away from IntelliJ it will trigger an update and will then trigger a browser refresh

> 📹 Here's a [video going step by step through these instructions](https://www.loom.com/share/74183c76d45c416e870ccf7aa06dd8ee?sid=a3bf01f9-b22d-423a-b61c-050bb0620d02).

## Development Setup

### Setup Application

- If you have never set up a form flow application on your machine before start by following the
  instructions from the [form-flow library here.](https://github.com/codeforamerica/form-flow#intellij-setup)

Once you've done that, follow the steps below:

1. The `setup.sh` script you ran earlier will have installed the necessary dependencies for you in
addition to creating the databases and database users needed for the application.
2. Run the application using the `IlGCCApplication` configuration (found in org.ilgcc.app). The run 
will fail, but it will create a new run configuration in your IntelliJ IDE. Open this new run 
configuration and configure it to use the `.env` file you created earlier with the EnvFile plugin.
Run -> Edit Configurations -> IlGCCApplication. Select `Enable EnvFile` and manually add the `.env`
file you created using the `+` button.
3. Make sure the `SPRING_PROFILES_ACTIVE` is set to `dev` in the `.env` file.
4. Run the application again. The application should start up successfully.

### Contributing Live Templates to Your App

If you have created live templates with fragments which are specific to your application based on a
starter app template, you can commit them to your repository. You will follow a similar pattern to
create templates to what is outlined
[in the form-flow library here.](https://github.com/codeforamerica/form-flow#contribute-new-live-templates)

An example template which was set up using this process, starting from an html snippet is available
[in this repository's IntelliJ settings folder](intellij-settings/StarterAppLiveTemplate.xml).

### Using a Local Version of the Form-Flow Library (For Form-Flow Library Developers)

To use a local version of the  [form-flow](https://github.com/codeforamerica/form-flow) library you
can do the following:

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

# Using This as a Template Repository

## Actuator Endpoints

The [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) is a
feature Spring Boot provides to monitor and interact with your application. It opens endpoints that can be queried to get information
about your application, like health and build information.

The IL GCC app, by default, enables full access to all actuator endpoints in the dev profile for
local development. However, in other profiles, it restricts access, allowing only the health and
build information endpoints to be available for use outside of local development.

** ⚠️ This feature can be a large security concern if certain endpoints are open. ⚠️**

Please read the following section in
our [Form Flow Library's documentation](https://github.com/codeforamerica/form-flow#actuator-endpoints)
as well as Spring
Boot's [documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html).
It's extremely important that you understand the actuator and what it provides and the risks, so
that you can configure the feature accordingly.

## Scripts

We provide a directory named `scripts` where we place small scripts we think are
useful for people using our library. Below are descriptions of the scripts
located in that directory.

### setup.sh

This script will ensure that all dependencies are installed (using `Homebrew`):
- `jenv` to manage Java
- The right `Java` version
- `PostgreSQL`

It will also create a localhost cert so that local develop can happen over `https`.

Next, it will create the database needed for running tests, and then run the tests.

### generate_localhost_cert.sh

This script will install dependencies to create an SSL certification so that you can use `https` with `localhost`.

To run this script, from the root of this project:

```bash
sh ./scripts/generate_localhost_cert.sh
```

### generate_migration.sh

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
$> ./generate_migration.sh
```

_Note: if it does not run on the command line, you may need to give the script executable
permission first, by running `chmod u+x generate_migration.sh`_

## AWS Setup

1. Provision a new AWS bucket.
    1. We use two buckets: one for demo purposes and another for production.
2. Replace the bucket names with your newly created buckets in
   the [main application configuration](src/main/resources/application.yaml) and
   the [demo application configuration](src/main/resources/application-demo.yaml).

## Cloud deployment

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

### Deployment in Aptible to a Custom URL

These instructions guide you through the process of creating resources in Aptible + AWS which will
allow you to deploy your application to a custom domain with root domain forwarding. Please create
the resources in the specified order, as there are
dependencies.

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

# Troubleshooting IntelliJ

## Application Won't Run After IntelliJ Update

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
