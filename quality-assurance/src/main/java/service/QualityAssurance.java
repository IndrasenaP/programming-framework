package service;

import com.google.common.collect.ImmutableList;
import ethereum.SmartSocietyApplication;
import parliament.QAVoter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QualityAssurance {

    //private ImmutableList<QAVoter> voters = ImmutableList.copyOf(Stream.generate(QAVoter::new).limit(5).collect(Collectors.toList()));

    private SmartSocietyApplication smartSocietyApplication;

    public SmartSocietyApplication getSmartSocietyApplication() {
        return smartSocietyApplication;
    }

    public void setSmartSocietyApplication(SmartSocietyApplication smartSocietyApplication) {
        this.smartSocietyApplication = smartSocietyApplication;
    }
}
