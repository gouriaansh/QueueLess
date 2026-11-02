package org.example;

public class Token {

    private int tokenNumber;
    private String studentName;
    private String status;

    public Token(int tokenNumber, String studentName, String status) {

        this.tokenNumber = tokenNumber;
        this.studentName = studentName;
        this.status = status;

    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStatus() {
        return status;
    }

}

