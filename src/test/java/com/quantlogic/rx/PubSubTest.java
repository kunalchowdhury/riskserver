package com.quantlogic.rx;

import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class PubSubTest {
    public static void main(String[] args) {
        PublishSubject<String> publishSubject = PublishSubject.<String>create();

        publishSubject.map(String::length).subscribe(System.out::println);

        publishSubject.onNext("Alpha");
        publishSubject.onNext("Beta");
        publishSubject.onCompleted();


    }
}
