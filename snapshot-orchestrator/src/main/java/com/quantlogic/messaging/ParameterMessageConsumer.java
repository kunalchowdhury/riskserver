package com.quantlogic.messaging;

import com.quantlogic.common.message.MarkerAndAddressResevationMessage;
import com.quantlogic.snapshot.SnapshotWindowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ParameterMessageConsumer {

    private final PublishSubject<MarkerAndAddressResevationMessage> publishSubject;

    public ParameterMessageConsumer(@Autowired SnapshotWindowManager snapshotWindowManager) {
        publishSubject = PublishSubject.create();
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        publishSubject
                .observeOn(Schedulers.from(newSingleThreadExecutor))
                .doOnNext(markerMessage ->
                {
                    String tag = markerMessage.getId().split("\\|")[1];
                    if(markerMessage.isFreeAddress()){
                         snapshotWindowManager.reserveAddress(markerMessage.getAddressLoc(), true);
                    }else if(markerMessage.isReserveAddress()){
                        snapshotWindowManager.reserveAddress(markerMessage.getAddressLoc(), false);
                    }else if(markerMessage.isCloseBucket()) {
                        snapshotWindowManager.closeBucket(tag);
                    }else {
                        SnapshotWindowManager.ParameterType parameterType =
                                SnapshotWindowManager.ParameterType.valueOf(markerMessage.getId().split("\\|")[0]);
                        snapshotWindowManager.addToBucket(parameterType, tag, markerMessage.getId(), markerMessage.getVersion() );
                    }
                });

    }

    public PublishSubject<MarkerAndAddressResevationMessage> getPublishSubject() {
        return publishSubject;
    }
}
