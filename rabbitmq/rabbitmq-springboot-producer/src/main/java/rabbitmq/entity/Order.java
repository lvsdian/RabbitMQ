package rabbitmq.entity;

import java.io.Serializable;

/**
 * @description:
 * @author:LSD
 * @when:2019/12/18/15:43
 */
public class Order implements Serializable {

    private String id;
    private String name;

    public Order(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
