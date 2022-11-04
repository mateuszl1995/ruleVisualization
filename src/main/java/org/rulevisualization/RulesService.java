package org.rulevisualization;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.rulelearn.data.Attribute;
import org.rulelearn.data.EvaluationAttribute;
import org.rulelearn.data.InformationTable;
import org.rulelearn.data.InformationTableBuilder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class RulesService {

    public InformationTable loadInformationTable(Attribute[] attributes, InputStream examplesStream) throws IOException, FileNotFoundException {
        List<String []> objects;
        InformationTableBuilder informationTableBuilder;

        try(JsonReader jsonObjectsReader = new JsonReader(new InputStreamReader(examplesStream))) {
            org.rulelearn.data.json.ObjectBuilder ob = new org.rulelearn.data.json.ObjectBuilder.Builder(attributes).build();
            objects = ob.getObjects(JsonParser.parseReader(jsonObjectsReader));
        }
        informationTableBuilder = new InformationTableBuilder(attributes, new String[] {org.rulelearn.data.json.ObjectBuilder.DEFAULT_MISSING_VALUE_STRING});
        if (objects != null) {
            for (int i = 0; i < objects.size(); i++) {
                informationTableBuilder.addObject(objects.get(i));
            }
            for (int i = 0; i < attributes.length; i++) {
                if (attributes[i] instanceof EvaluationAttribute) {
                    ((EvaluationAttribute) attributes[i]).getValueType().getCachingFactory().clearVolatileCache();
                }
            }
        }
        return informationTableBuilder.build();
    }

}
