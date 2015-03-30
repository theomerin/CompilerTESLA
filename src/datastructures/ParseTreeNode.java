package datastructures;

import lexicalanalyzer.*;

public class ParseTreeNode {
    public Token token;
    public ParseTreeNode firstChild;
    public ParseTreeNode nextSibling;
    
    public ParseTreeNode() {
        this(new Token(), null, null);
    }
    
    public ParseTreeNode(Token token, ParseTreeNode child, ParseTreeNode sibling) {
        this.token = token;
        this.firstChild = child;
        this.nextSibling = sibling;
    }
    
    public void createParseTreeNode(Token token, ParseTreeNode child, ParseTreeNode sibling) {
        this.token = token;
        this.firstChild = child;
        this.nextSibling = sibling;
    }
    
    public void setToken(Token token) {
        this.token = token;
    }
    
    public void setFirstChild(ParseTreeNode child) {
        this.firstChild = child;
    }
    
    public void setNextSibling(ParseTreeNode sibling) {
        this.nextSibling = sibling;
    }
    
    public void setChildAndSibling(ParseTreeNode child, ParseTreeNode sibling) {
        this.firstChild = child;
        this.nextSibling = sibling;
    }
    
    public Token getToken() {
        return this.token;
    }
}