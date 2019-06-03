package com.synet.net.route;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

public class PredicateDefinition {

    @NotNull
    private String name;

    private int beginCmd;

    private int endCmd;

    public PredicateDefinition() {
    }

    public PredicateDefinition(String text) {
        int eqIdx = text.indexOf('=');
        if (eqIdx <= 0) {
            throw new ValidationException("Unable to parse PredicateDefinition text '" + text + "'" +
                    ", must be of the form name=value");
        }
        setName(text.substring(0, eqIdx));

        String[] args = tokenizeToStringArray(text.substring(eqIdx+1), "~");
        if (args.length == 0) {
            throw new ValidationException("Unable to parse PredicateDefinition text '" + text + "'" +
                    ", must be of the form name=value");
        }
        if (args.length == 1) {
            setBeginCmd(Integer.parseInt(args[0]));
            setEndCmd(getBeginCmd());
        }
        if (args.length == 2) {
            setBeginCmd(Integer.parseInt(args[0]));
            setEndCmd(Integer.parseInt(args[1]));
        }
        if (getEndCmd() < getBeginCmd()) {
            throw new ValidationException("Unable to parse PredicateDefinition text '" + text + "'" +
                    ", must be of endCmd >= beginCmd");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBeginCmd() {
        return beginCmd;
    }

    public void setBeginCmd(int beginCmd) {
        this.beginCmd = beginCmd;
    }

    public int getEndCmd() {
        return endCmd;
    }

    public void setEndCmd(int endCmd) {
        this.endCmd = endCmd;
    }
}
