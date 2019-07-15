package com.synet.net.data.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StateEntityHolder {

    public final static int STATE_SAVE = 0;
//    public final static int STATE_UPDATE = 1;
    public final static int STATE_DELETE = 2;

    private StateEntity entity;

    private int state;

}
