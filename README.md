# Dictionary_SpringBootApp

Implemented CRUD operations with entities: Word; PartOfSpeech; Language.

H2 is used as a database.

TranslateRelation – additional entity for the relation of words between different languages (the language in this entity is solely to speed up the search).

When a word translation is added, it is assigned a 'UUID'. Similar words in different languages are assigned the same 'UUID'.

e.g.: 
        
        ('cry', 'en') //id=1
        ('орать', ru) //id=2
        ('кричать', 'ru') //id=3
        ('крычаць', 'by') //id=4 
        
        ===REQUEST===
        PATCH: /translates 
        {"wordId1": 4, "wordId2": 2}
        ===RESPONSE===
        {"translateRelationUUID": "bf02d8a9-3287-49d6-bb7f-215fcf20693f"}
        
        ===REQUEST===
        PATCH: /translates 
        {"wordId1": 3, "wordId2": 4}
        ===RESPONSE===
        {"translateRelationUUID": "aba87d91-3547-4a06-8c21-9c9db981cfa3"}
        
        ===REQUEST===
        PATCH: /translates 
        {"wordId1": 2, "wordId2": 3}
        ===RESPONSE===
        {"translateRelationUUID": "bf02d8a9-3287-49d6-bb7f-215fcf20693f"}
   
        
        ===REQUEST===
        GET: /translates?spelling=cry&langFrom=en&langTo=ru
        ===RESPONSE===
        {  
        ...
           "translateRelationUUID":"bf02d8a9-3287-49d6-bb7f-215fcf20693f",
           "translateWordList":[  
              {  
                 "wordId":2,
                 "spelling":"орать",
                 "language":{...},
                 "partOfSpeech":{...},
                 "meaning":"Громко кричать, слишком громко разговаривать",
                 "_links":{...}
              },
              {  
                 "wordId":3,
                 "spelling":"кричать",
                 "language":{...},
                 "partOfSpeech":{...},
                 "meaning":"Громко говорить",
                 "_links":{...}
              }
           ]
         ...
        }

Developed integration tests focus on all layers of the application and one test for testing the service and persistence layer