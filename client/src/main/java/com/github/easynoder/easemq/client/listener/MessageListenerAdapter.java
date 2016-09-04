package com.github.easynoder.easemq.client.listener;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/4
 * E-mail:easynoder@outlook.com
 */
public interface MessageListenerAdapter extends MessageListener {

    /**
     * 目前 监听器继续依赖topic吧..topic==queue-name
     * 暂时先这样吧, 后续再继续优化,把topic和queue-name拆分出来
     *
     * @return
     */
    public String getTopic();
}
