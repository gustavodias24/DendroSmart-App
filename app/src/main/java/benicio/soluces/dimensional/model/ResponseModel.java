package benicio.soluces.dimensional.model;

public class ResponseModel  {
    boolean success;
    String msg;

    public boolean issuccess() {
        return success;
    }

    public void setsuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
