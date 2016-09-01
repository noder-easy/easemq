package com.github.easynoder.easemq.core.protocol;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/31
 * E-mail:easynoder@outlook.com
 */
public class AckMessage extends GenerateMessage {

    private boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    public AckMessage setSuccess(boolean success) {
        isSuccess = success;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AckMessage{");
        sb.append("header='").append(super.getHeader()).append('\'');
        sb.append(", body='").append(super.getBody()).append('\'');
        sb.append(", isSuccess=").append(isSuccess);
        sb.append('}');
        return sb.toString();
    }
}
