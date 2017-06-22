# Mail-Crawler
A simple Mail Crawler to extract the mails from a Mailing List and downloads them into an output folder.
The website crawled is http://mail-archives.apache.org/mod_mbox/maven-users/.
The website is specified in the program but the option to specify it from the command line will be provided soon.
The mail contents will be downloaded into folders according to date and month and stored in format as "Author-Date" format.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

* Java version 1.8 or higher
* Apache Commons

### Installing

* Install latest version of [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* Download the jar files for [Apache Commons](https://commons.apache.org/proper/commons-io/download_io.cgi)
* Download or Clone the repository
  ```
  git clone https://github.com/anmolvarshney05/Mail-Crawler.git
  ```
* Copy the jar files to src Folder.

### Running

* Open Terminal
* Move to the directory where repository was cloned
  ```
  cd Mail-Crawler/src
  ```
* Compile Java file with dependencies
  ```
  javac -classpath ".;commons-io-2.5/commons-io-2.5.jar" Crawler.java
  ```
* Execute the generated file
  ```
  java Crawler.class
  ```

## Built With

* [Java](https://www.java.com/) - Programming Language

## Versioning

* Version 1.0
  * Extracts Mail URL's from given URL

## Authors

* **Anmol Varshney**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE.md) file for details

## Acknowledgments

* Work done as an assignment during the work at Sprinklr

