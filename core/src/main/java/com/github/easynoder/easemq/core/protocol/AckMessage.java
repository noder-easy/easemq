package com.github.easynoder.easemq.core.protocol;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/31
 * E-mail:easynoder@outlook.com
 */
public class AckMessage extends GenerateMessage {

    private boolean isSuccess;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AckMessage{");
        sb.append("isSuccess=").append(isSuccess);
        sb.append('}');
        return sb.toString();
    }
}
