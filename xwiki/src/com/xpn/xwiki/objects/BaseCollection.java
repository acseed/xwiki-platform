/**
 * ===================================================================
 *
 * Copyright (c) 2003 Ludovic Dubost, All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details, published at
 * http://www.gnu.org/copyleft/lesser.html or in lesser.txt in the
 * root folder of this distribution.
 *
 * Created by
 * User: Ludovic Dubost
 * Date: 9 d�c. 2003
 * Time: 11:36:06
 */
package com.xpn.xwiki.objects;


import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.objects.classes.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.io.Serializable;

public abstract class BaseCollection extends BaseElement implements ObjectInterface, Serializable {
    private BaseClass xWikiClass;
    private Map fields = new HashMap();
    private String className;


    public int getId() {
            return (getName()+getClassName()).hashCode();
        }

    public void setId(int id) {
    }

    public String getClassName() {
        if (xWikiClass!=null)
         return xWikiClass.getName();
        else
         return (className == null) ? "" : className;
    }

    public void setClassName(String name) {
        className = name;
    }

    public Object[] getProperties() {
        Object[] array = getFields().values().toArray();
        return array;
    }

    public Object[] getPropertyNames() {
        Object[] array = getFields().keySet().toArray();
        return array;
    }

    public void checkField(String name) throws XWikiException {
        if (getxWikiClass().safeget(name)==null) {
            Object[] args = { name, getxWikiClass().getName() };
            throw new XWikiException( XWikiException.MODULE_XWIKI_CLASSES, XWikiException.ERROR_XWIKI_CLASSES_FIELD_DOES_NOT_EXIST,
                    "Field {0} does not exist in class {1}", null, args);
        }
    }

    public ElementInterface safeget(String name) {
        return (ElementInterface) getFields().get(name);
    }

    public ElementInterface get(String name) throws XWikiException {
        checkField(name);
        return safeget(name);
    }

    public void safeput(String name,ElementInterface property) {
        getFields().put(name, property);
        if (property instanceof BaseProperty) {
         ((BaseProperty)property).setObject(this);
         ((BaseProperty)property).setName(name);
        }
    }

    public void put(String name,ElementInterface property) throws XWikiException {
        checkField(name);
        safeput(name, property);
    }


    public BaseClass getxWikiClass() {
        return xWikiClass;
    }

    public void setxWikiClass(BaseClass xWikiClass) {
        this.xWikiClass = xWikiClass;
    }

    public String getStringValue(String name) {
        StringProperty prop = (StringProperty)safeget(name);
        if (prop==null)
         return "";
        else
         return (String)prop.getValue();
    }

    public void setStringValue(String name, String value) {
        StringProperty property = new StringProperty();
        property.setValue(value);
        safeput(name, property);
    }

    public int getIntValue(String name) {
        NumberProperty prop = (NumberProperty)safeget(name);
        if (prop==null)
         return 0;
        else
         return ((Number)prop.getValue()).intValue();
    }

    public void setIntValue(String name, int value) {
        NumberProperty property = new NumberProperty();
        property.setValue(new Integer(value));
        safeput(name, property);
    }

    public Map getFields() {
        return fields;
    }

    public void setFields(Map fields) {
        this.fields = fields;
    }

    public String toString() {
        return getClass().getName();
    }

    public boolean equals(Object coll) {
     if (!super.equals(coll))
      return false;
     BaseCollection collection = (BaseCollection) coll;
     if (collection.getClassName()==null) {
         if (getClassName()!=null)
         return false;
     } else if (!collection.getClassName().equals(getClassName()))
         return false;

     if (getFields().size()!=collection.getFields().size())
         return false;

     Iterator itfields = getFields().keySet().iterator();
     while (itfields.hasNext()) {
       String name = (String) itfields.next();
       Object prop = getFields().get(name);
       Object prop2 = collection.getFields().get(name);
       if (!prop.equals(prop2))
        return false;
     }

     if (this instanceof BaseObject) {
     BaseClass bclass = getxWikiClass();
     if (bclass==null) {
         if (collection.getxWikiClass()!=null)
             return false;
     } else {
         if (!bclass.equals(collection.getxWikiClass()))
             return false;
     }
     }

     return true;
    }

    public Object clone() {
        BaseCollection collection = (BaseCollection) super.clone();
        collection.setClassName(getClassName());

        if (this instanceof BaseObject) {
          if (getxWikiClass()!=null)
            collection.setxWikiClass((BaseClass)getxWikiClass().clone());
        }

        Map fields = getFields();
        Map cfields = new HashMap();
        Iterator itfields = fields.keySet().iterator();
        while (itfields.hasNext()) {
            String name = (String)itfields.next();
            PropertyInterface prop = (PropertyInterface)((BaseElement)fields.get(name)).clone();
            prop.setObject(collection);
            cfields.put(name, prop);
        }
        collection.setFields(cfields);
        return collection;
    }

    public void merge(BaseObject object) {
        Iterator itfields = object.getFields().keySet().iterator();
        while (itfields.hasNext()) {
          String name = (String) itfields.next();
          if (safeget(name)==null)
              safeput(name, (ElementInterface) ((BaseElement)object.safeget(name)).clone());
        }
    }

}
