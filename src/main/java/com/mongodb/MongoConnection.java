package com.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.net.URI;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class MongoConnection {
	public MongoConnection() throws Exception {
	}

	MongoClient client;

	ConnectionString uri = new ConnectionString("mongodb://localhost:27017");

	public void connect() {
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(uri)
				.addCommandListener(new CommandCounter()).build();
		client = MongoClients.create(settings);
		System.out.println(" Connected to mongo successfully!");
	}

	public void dbase() {

		MongoDatabase database = client.getDatabase("test");
		MongoCollection<Document> res = database.getCollection("restaurants");
		Document doc = res.find(new Document("borough", "Bronx")).first();
		System.out.println(doc.toJson());

	}

}
