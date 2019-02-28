package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;

/**
 * 用户项目列表信息
 *
 */
public class CloudProjectListInfo extends BaseModel {


    /**
     * status : success
     * code : 200
     * message :
     * data : {"data_":[{"id":3,"name":"定时器主机","machine_code":"39-24-33-12-1c-15-50-e3-d7-af","mac":"42-4C-45-00-B3-87","ip":"172.16.13.112","active_status":2}]}
     */

    private String status;
    private int code;
    private String message;
    private DataBeanX data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        private List<DataBean> data_;

        public List<DataBean> getData_() {
            return data_;
        }

        public void setData_(List<DataBean> data_) {
            this.data_ = data_;
        }

        public static class DataBean {
            /**
             * id : 3
             * name : 定时器主机
             * machine_code : 39-24-33-12-1c-15-50-e3-d7-af
             * mac : 42-4C-45-00-B3-87
             * ip : 172.16.13.112
             * active_status : 2
             */

            private int id;
            private String name;
            private String machine_code;
            private String mac;
            private String ip;
            private int active_status;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMachine_code() {
                return machine_code;
            }

            public void setMachine_code(String machine_code) {
                this.machine_code = machine_code;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }

            public String getIp() {
                return ip;
            }

            public void setIp(String ip) {
                this.ip = ip;
            }

            public int getActive_status() {
                return active_status;
            }

            public void setActive_status(int active_status) {
                this.active_status = active_status;
            }
        }
    }
}
