# \#fr Projet Logiciel Supélec
**Séquence 6 - 2014/2015**

A functional version [Super Sprint](http://www.giantbomb.com/super-sprint/3030-2776/)-like game.

This project was developed as Supélec engineering course software project from Nov. 2014 to Feb. 2015.

## Compiling ##

Make sure that you have Java JDK installed in your computer and then just run the Java compiler command
```
java @./java-files.txt
```
that compiles all files listed on `java-files.txt`.

You can update your files list by typing (anyway a default file is given in)
```
javac $(find . -name \*.java)
```
**Note:** you must change the current directory to your project source folder before run the command to update filename list.
