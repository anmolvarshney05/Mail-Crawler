# Mail-Crawler
* A simple Mail Crawler to extract the mails and their attachment(s) from a Mailing List and downloads them into an output folder.
* The Crawler works with only http://mail-archives.apache.org/ as the base URL.
* The website crawled is http://mail-archives.apache.org/mod_mbox/maven-users/.
* The default website is specified in the program, it can also be specified from the command line.
* The mail contents will be downloaded into folders according to date, month and author and stored in format as "Year/Month/Author/Author DateStamp.txt".
* The attachment(s) will be downloaded into a sub folder in the same folder as the mails from an author in the format as "Year/Month/Author/Author DateStamp Attachments/Attachment (number).txt".
* The default save directory is the Desktop and is specified in the program, it can also be specified from command line.
* The command line options can be used in gradle run as well. 
* A UI is also provided to run the Crawler and to specify the URL and the save Directory.
* Crawler resumes from last endpoint if run with the same configuration as before.

## Getting Started

* These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
* Instructions for gradle are specified for running the UI but the project can also run from command line by changing mainClass file in build.gradle.

### Prerequisites

* Java version 1.8 or higher
* Apache Commons
* Gradle

### Installing
#### Building from source 

* Install latest version of [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Download the jar files for [Apache Commons](https://commons.apache.org/proper/commons-io/download_io.cgi)
* Download or Clone the repository
  ```
  git clone https://github.com/anmolvarshney05/Mail-Crawler.git
  ```
* Copy the jar files to src Folder.

#### Using gradle

* Install [gradle](https://gradle.org/install)
* Download or Clone the repository
  ```
  git clone https://github.com/anmolvarshney05/Mail-Crawler.git
  ```
* Open Terminal
* Move to the directory where repository was cloned
  ```
  cd Mail-Crawler
  ```
* gradle build
  ```
  gradle build
  ```

### Running
#### Running from source

* Open Terminal
* Move to the directory where repository was cloned
  ```
  cd Mail-Crawler/src/main/java
  ```
##### Command Line Method
###### Compile Java file with dependencies
  ```
  javac -classpath ".;commons-io-2.5/commons-io-2.5.jar" Crawler.java
  ```
###### Execute the generated file
  ```
  java Crawler.class [Optional Argument 1] [Optional Argument 2]
  Optional Argument 1 - base URL
  Optional Argument 2 - save directory
  ```
##### User Interface Method
###### Compile Java file with dependencies
  ```
  javac -classpath ".;commons-io-2.5/commons-io-2.5.jar" UserInterface.java
  ```
###### Execute the generated file
  ```
  java UserInterface.class
  ```
 
#### Using gradle

* Open Terminal
* Move to the directory where repository was cloned
  ```
  cd Mail-Crawler
  ```
* gradle run
  ```
  gradle run
  ```

## Built With

* [Java](https://www.java.com/) - Programming Language
* [Gradle](https://www.gradle.org/) - Build Tool

## Contributing

To contribute to Mail-Crawler please fork the project, make your changes locally,
and open a pull request. We'll ask that you add the following information and
adhere to the following conventions.

1. Include a summary and description of why this change is necessary.
   Describing not just _what_ but also _why_ provides context that will help
   reviewers review your change. Please cite relevant issues, if applicable.
2. Please squash your commits into a single commit before submitting a pull
   request. This keeps our change log clean.
3. Please be respectful to everyone involved with the project, including those
   that have opened issues. We do our best to serve everyone, but remember that
   if there is a problem or something missing for you, you're likely the best
   candidate to fix it.
4. Please be patient while we review your changes.


## Versioning

* Version 1.0
  * Extracts Mail URL's from given URL
* Version 1.1
  * Extracts Mail contents and stores them in a structured format
* Version 2.0
  * Can be built with Gradle and used on any machine
* Version 2.1
  * User Interface for specifying Base URL and Resume operation

## Authors

* **Anmol Varshney**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details

## Acknowledgments

* Work done as an assignment during the work at Sprinklr
