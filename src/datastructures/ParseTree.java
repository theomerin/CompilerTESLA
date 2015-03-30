package datastructures;

public class ParseTree {
    public ParseTreeNode root;
    
    public ParseTree() {
        this.root = null;
    }
    
    public ParseTree(ParseTreeNode root) {
        this.root = root;
    }
    
    public void setRoot(ParseTreeNode root) {
        this.root = root;
    }
    
    public String printTree(ParseTreeNode node) {
        if (node != null) {
            return "[ " + node.getToken().getTokenName().toString() + " f = " + printTree(node.firstChild) + " s = " + printTree(node.nextSibling) + "]";
        } else {
            return "";
        }
    }
    
    public String printParseTree() {
        if (this.root != null) {
            return "[ " + this.root.getToken().getTokenName().toString() + " f = " + printTree(this.root.firstChild) + " s = " + printTree(this.root.nextSibling) + " ]";
        } else {
            return "";
        }
    }
}
