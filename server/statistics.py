import json


def get_statistics(name: str, language: str):
    if language == "it":
        res = {"description": "Non ci sono ancora informazioni riguardo a " + name.upper() +
                              " sul server. Stiamo lavorando per sviluppare questa funzionalità il prima possibile.\n"
                              "Grazie per la comprensione."}
    else:
        res = {"description": "There is still no additional information about " + name.upper() +
                              " in the server. We are working to develop this feature as soon as possible.\n"
                              "Thanks for your understanding."
               }
    return json.dumps(res, ensure_ascii=False)
