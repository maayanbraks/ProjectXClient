package com.example.malicteam.projectxclient.Common.Callbacks;

import java.util.List;

import ResponsesEntitys.ProtocolLine;

/**
 * Created by Charcon on 31/05/2018.
 */

public interface EventDetailCallback {
    void onSuccees(List<ProtocolLine> list);
}
