/**
 * Copyright 2013 Ernestas Vaiciukevicius (ernestas.vaiciukevicius@gmail.com)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. *
 */
package com.ev.contactsmultipicker;

import java.io.Serializable;
import java.util.List;

/**
 * 联系人结果对象。包括了联系人的姓名。以及相关的手机号码（集合的形式。因为可能有多个）
 *
 * @author Ernestas Vaiciukevicius (ernestas.vaiciukevicius@gmail.com)
 */
public class ContactResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private String contactId;//联系人id
    private String name;//联系人姓名
    private List<ResultItem> results;

    public ContactResult(String id, String name, List<ResultItem> results) {
        this.name = name;
        this.contactId = id;
        this.results = results;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public List<ResultItem> getResults() {
        return results;
    }

    public void setResults(List<ResultItem> results) {
        this.results = results;
    }

    /**
     * 联系人的联系方式对象。包含了号码和号码类型
     */
    public static class ResultItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String result;//号码（多半为手机号码）
        private int resultKind;//号码类型

        public ResultItem(String result, int kind) {
            this.result = result;
            this.resultKind = kind;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public int getResultKind() {
            return resultKind;
        }

        public void setResultKind(int resultKind) {
            this.resultKind = resultKind;
        }
    }

    ;


}
