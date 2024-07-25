package benicio.soluces.dimensional.model;

import java.io.Serializable;

public class ResponseMsg implements Serializable {

    String msg;

    public ResponseMsg() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
