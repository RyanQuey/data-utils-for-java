package com.ryanquey.datautils.models;

// Considering using this to let model classes inherit from
// Debugging hints: 
// - Error: "PRIMARY KEY column "____" cannot be restricted (preceding column "___" is not restricted"
//    * make sure the primary keys are labelled correctly. Java driver will do a select under the covers as it initializes, and if fields are not labelled correctly or are not all set, it will give this error. 
public interface Record {
}
