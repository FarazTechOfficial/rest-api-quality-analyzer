import json
import urllib.request

spec = json.load(open(r'D:\spring\Research\sample-apis\my-api.json', 'r', encoding='utf-8'))
body = json.dumps({'apiName': 'My Spring Boot API', 'specification': json.dumps(spec)}).encode('utf-8')
req = urllib.request.Request('http://localhost:8081/api/analyze', data=body, headers={'Content-Type': 'application/json'})
resp = urllib.request.urlopen(req)
result = json.loads(resp.read())

open(r'D:\spring\Research\sample-apis\analysis-result.json', 'w', encoding='utf-8').write(json.dumps(result, indent=2))

print(f"Score: {result['score']}%")
print(f"Passed: {result['passedRules']}  Failed: {result['failedRules']}  Skipped: {result['skippedRules']}")
print()
print("VIOLATIONS:")
for r in result['results']:
    if not r['passed']:
        print(f"  [{r['ruleId']} | {r['practiceId']}] {r['endpoint']} {r['method']} - {r['message']}")
        if r['recommendation']:
            print(f"    -> {r['recommendation']}")
