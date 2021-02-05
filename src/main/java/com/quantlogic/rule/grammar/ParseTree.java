package com.quantlogic.rule.grammar;

import java.util.Arrays;

public class ParseTree {
    static class ParseNode{
        String value;
        ParseNode left, right;

        public ParseNode(String value) {
            this.value = value;
        }

        public static void inOrder(ParseNode node){
            if(node == null){
                return;
            }
            if(Arrays.asList("&&&", "|||").contains(node.value)){
                System.out.println("(");
            }
            inOrder(node.left);
            System.out.println(node.value);
            inOrder(node.right);
            if(Arrays.asList("&&&", "|||").contains(node.value)){
                System.out.println(")");
            }
        }

        public ParseNode getLeft() {
            return left;
        }

        public void setLeft(ParseNode left) {
            this.left = left;
        }

        public ParseNode getRight() {
            return right;
        }

        public void setRight(ParseNode right) {
            this.right = right;
        }

    }
}
