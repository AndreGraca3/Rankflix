# Rankflix Console Application

<img src="https://i.imgur.com/0zPGFfd.png" alt="Rankflix Logo" width="100"/>

This is a simplified version of Rankflix that uses console I/O instead of a graphical user interface.
The program containing a list of movies and their ratings, and allows the user to rate media and generate a graphical
representation
of the ratings that can then be posted to a Discord channel.

## Features 🌟

- View a list of movies and their ratings
- Rate a movie
- Generate a graphical representation of the ratings
- Post the graphical representation to a Discord channel
- Save the ratings to a file

### Prerequisites 📋

- [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) or higher
- Discord channel with a webhook URL

### Installation 🔧

1. Clone the repository
   ```sh
   git clone
    ```
2. Compile the program
    ```sh
    javac RankflixConsole.java
    ```
3. Run the program
   ```sh
   java Rankflix
   ```

## Why a console app? 🤔

It's a quick and easy way to interact with the program. It's also a
great
way to run the program without the need for a web server or other infrastructure.
Ratings will be saved to a file so that they can be loaded the next time the program is run.
