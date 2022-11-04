package org.rulevisualization;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.rulelearn.data.Attribute;
import org.rulelearn.data.EvaluationAttribute;
import org.rulelearn.data.IdentificationAttribute;
import org.rulelearn.data.json.AttributeDeserializer;
import org.rulelearn.data.json.EvaluationAttributeSerializer;
import org.rulelearn.data.json.IdentificationAttributeSerializer;
import org.rulelearn.rules.ruleml.RuleParser;
import org.rulevisualization.serializers.RuleSetWithCharacteristicsSerializer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class RulesController {

	private final RulesService service;

	@GetMapping
	public String test() {
		return "Test OK";
	}
	
	@PostMapping
	public String load(
			@RequestParam("attributes") MultipartFile attributesFile,
			@RequestParam("rules") MultipartFile rulesFile,
			@RequestParam("examples") MultipartFile examplesFile
	) throws IOException {

		var attributesString = new String(attributesFile.getBytes());

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Attribute.class, new AttributeDeserializer());
		gsonBuilder.registerTypeAdapter(IdentificationAttribute.class, new IdentificationAttributeSerializer());
		gsonBuilder.registerTypeAdapter(EvaluationAttribute.class, new EvaluationAttributeSerializer());
		Gson gson = gsonBuilder.create();

		var attributes = gson.fromJson(attributesString, Attribute[].class);
		var ruleSet = new RuleParser(attributes).parseRulesWithCharacteristics(rulesFile.getInputStream()).get(1);

		RuleSetWithCharacteristicsSerializer serializer = new RuleSetWithCharacteristicsSerializer();
		JsonElement jsonRules = serializer.serialize(ruleSet, null, null);

		JsonArray examples = new JsonArray();
		if (examplesFile != null) {
			examplesFile.getInputStream();
			var examplesStream = examplesFile.getInputStream();
			var informationTable = service.loadInformationTable(attributes, examplesStream);
			for (int e = 0; e < informationTable.getNumberOfObjects(); e++) {
				JsonObject example = new JsonObject();
				JsonArray rules = new JsonArray();
				for (int i = 0; i < ruleSet.size(); i++) {
					if (ruleSet.getRule(i).covers(e, informationTable))
						rules.add(i);
				}
				example.addProperty("id", e);
				example.add("rules", rules);
				examples.add(example);
			}
		}

		JsonObject json = new JsonObject();
		json.add("rules", jsonRules);
		json.add("examples", examples);
		return json.toString();
	}

}
