package com.quantlogic.messaging;

import com.quantlogic.common.message.MarkerAndAddressReservationMessage;
import com.quantlogic.common.message.Watermark;
import com.quantlogic.snapshot.SnapshotWindowManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ParameterMessageConsumer {
    private final Logger LOGGER = LoggerFactory.getLogger(ParameterMessageConsumer.class);
    private final PublishSubject<MarkerAndAddressReservationMessage> publishSubject;

    public ParameterMessageConsumer(@Autowired SnapshotWindowManager snapshotWindowManager) {
        publishSubject = PublishSubject.create();
        ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
        publishSubject.asObservable()
                .observeOn(Schedulers.from(newSingleThreadExecutor))
                .doOnNext(markerMessage ->
                {
                    LOGGER.info("Got marker message in publish suject {} ", markerMessage);
                    if(markerMessage == Watermark.INSTANCE){
                        snapshotWindowManager.processWatermark();
                    }else {
                        if (markerMessage.isFreeAddress()) {
                            LOGGER.info("*** Got FREE ADDRESS message {} **** ", markerMessage);
                            snapshotWindowManager.freeAddress(markerMessage.getId(), (int) markerMessage.getAddressLoc());
                        } else {
                            String tag = markerMessage.getId().split("\\|")[1];
                            SnapshotWindowManager.ParameterType parameterType =
                                    SnapshotWindowManager.ParameterType.valueOf(markerMessage.getId().split("\\|")[0]);
                            snapshotWindowManager.addToBucket(parameterType, tag, markerMessage.getId(), markerMessage.getVersion());
                            if (markerMessage.isCloseBucket()) {
                                snapshotWindowManager.closeBucket(tag);
                            }
                        }
                    }
                }).doOnError(throwable -> LOGGER.info("Error {} ", throwable.getMessage())).subscribe();

    }

    public PublishSubject<MarkerAndAddressReservationMessage> getPublishSubject() {
        return publishSubject;
    }
}
