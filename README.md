# Maybank Credit Card Statement Parser

This is a simple utility to parse information from your own Maybank Credit Card statement. 

Disclaimer: This utility is not affiliated officially with Maybank. Also, this utility only runs locally
in your machine and does not source the output outside of it. Nevertheless, please use this utility responsibly. 

## Purpose

This utility is useful for keeping track of your Maybank credit card transactions, mainly for post-mortem
purpose where you run out of money and suddenly have the urge to find the culprit (based
on true story).

Please note that this utility is not built for other type of statements for Maybank, so they may not work well here. 

## Input

Your Maybank credit card statements in PDF format. 

## Output

Excel file containing your credit card transactions (debit and credit). Only last 4-digit card number is processed and displayed, 
so you can differentiate between multiple cards. 

## Usage

Note that this utility requires Java installed in your machine. I'm sorry, I just don't
have time to compile it for OS-specific executables.

Steps:
1. Determine whether you have Java installed in your machine. Either check from application list in your OS, or run `java -v` in command prompt.
2. If Java is not installed, download Java installer and install it. This utility has been compiled with JDK 13, but I think it should work fine in any modern Java version.
3. Download the utility file from this Github release.
4. Login to your Maybank account, and download your credit card statements (you can use this utility with multiple statement files). Place them in an input folder.
5. Create a new folder in your machine as output folder.
6. Double-click on the utility jar file. Then, select the input folder containing the PDF files, and then select the output folder. 
7. Click Go.
8. If there is no issue, the output Excel file is created in the output folder


## Support

If you run into issues or if you have any suggestion/comment, feel free to
put into this repository's issue. 








