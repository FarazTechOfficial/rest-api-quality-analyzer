# Real-World OpenAPI Dataset

Reproducible corpus of **37 distinct real-world production API specifications**
used to evaluate the analyzer's paper-mapped rules. This directory is the
documented replacement for the development fixtures (`sample-apis/`) called out
in `../README.md`.

Every spec is a **byte-for-byte, unmodified copy** of the file served by the
APIs.guru openapi-directory at collection time. Nothing here is fabricated,
rewritten, or "fixed". The corpus exists to be labeled with manual ground truth
in a later step; **no experiment has been run and no ground-truth labels exist
yet** in this directory.

## Deliverables

| File | Purpose |
| --- | --- |
| `manifest.csv` | Final corpus: `api_id, api_name, source_url, local_file, openapi_version, endpoint_count` (37 rows) |
| `selection.csv` | Pinned selection: which API key + version was resolved and fetched |
| `specs/<api_id>/openapi.json` | The spec files, unmodified (original bytes from APIs.guru) |
| `fetch_dataset.py` | Reproducible acquisition pipeline (download + finalize) |
| `tool/` | Parser probe that validates every spec with the exact parser the Analyzer uses |

## Source

- **Provider**: APIs.guru openapi-directory (`https://api.apis.guru`)
- **Index**: `https://api.apis.guru/v2/list.json`
- **Spec files**: `https://api.apis.guru/v2/specs/<key>/<version>/openapi.json`
  (2.0 entries are served as `swagger.json`)
- **Collection date**: 2026-09-08
- Per-entry `updated` timestamps are recorded in `selection.csv`.
- `selection.csv` pins the resolved version keys, so a re-fetch deterministically
  retrieves the exact same versions even if the live index later changes.

## Selection criteria

### Inclusion

1. **Real-world production API** with public documentation, curated by APIs.guru.
2. **One representative spec per distinct product.** eBay and Google appear
   several times only because each entry is a genuinely distinct service
   (e.g. `ebay.com:sell-account` vs `ebay.com:sell-fulfillment`).
3. **Preferred version is served as JSON** (satisfied by all 2,529 index entries).
4. **OpenAPI 3.0.x** — the analyzer targets OpenAPI 3.
5. **Must parse with the Analyzer's parser** (swagger-parser `OpenAPIV3Parser`
   2.1.22, `resolve=true`); every row in `manifest.csv` passes.
6. **Domain diversity**: developer platforms, SaaS, payments, messaging,
   e-commerce, cloud, data/AI, weather, hotel booking.

### Exclusion

1. **Repository fixtures** under `sample-apis/`
   (`good-api`, `bad-api`, `mixed-api`, `my-api`, `request-body`) — never reused
   as dataset entries.
2. **Synthetic, demo, sandbox, or example APIs** (deliberately good/bad test
   corpora from the literature or benchmarks are out of scope).
3. **Duplicate specs** — deduplicated by normalized `info.title`; **0 duplicates
   found** among the 40 selected. The fetcher drops any later entry whose title
   repeats an already-selected one.
4. **Swagger 2.0 preferred docs** — 3 collected specs
   (`gitlab-com`, `instagram-com`, `weatherbit-io`) were **excluded after the
   parser probe**: `OpenApiParserService` reads with `OpenAPIV3Parser`, which
   returns a `null` OpenAPI model for Swagger 2.0 documents, so the Analyzer
   rejects them (`InvalidOpenApiException`). The files remain under `specs/`
   for reference but are **not** part of the corpus.
5. Anything the Analyzer's parser rejects — all 37 remaining specs pass.

## Counts

| Metric | Value |
| --- | --- |
| Index entries considered | 2,529 |
| Curated shortlist downloaded | 40 |
| Excluded (OpenAPI 2.0, parser-incompatible) | 3 |
| **Corpus size (manifest rows)** | **37** |
| Total endpoints (parser counts) | 4,624 |
| Total spec file size | 37.52 MB (40 files incl. the 3 preserved exclusions) |
| Duplicates removed | 0 |

OpenAPI version mix within the corpus: `3.0.0` × 29, `3.0.1` × 3, `3.0.2` × 2,
`3.0.3` × 3. No minimum endpoint threshold is imposed: the corpus intentionally
retains naturally small but real APIs (e.g. 1-endpoint `kgsearch`), preserving
the skewed size distribution of production APIs. Endpoint counts range from 1
to 845 per spec.

## `manifest.csv` columns

| Column | Meaning |
| --- | --- |
| `api_id` | Deterministic filesystem-safe slug of the APIs.guru key |
| `api_name` | `info.title` as parsed by the Analyzer's parser |
| `source_url` | Canonical APIs.guru spec URL (the exact file downloaded) |
| `local_file` | Path to the unmodified spec, relative to this directory |
| `openapi_version` | Version string from the Analyzer's parsed model |
| `endpoint_count` | Operations counted by the Analyzer's parser (GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS, `$ref`-resolved) |

## Reproduction

Prerequisites: Python 3.12+, Maven 3.9+, network on first run (to fetch the
APIs.guru index and, once, the parser probe's Maven dependencies).

```bash
# 1. (Re)resolve and (re)download all specs; writes selection.csv + preliminary manifest
python fetch_dataset.py download --force --force-index

# 2. Parse every spec with the analyzer's exact parser (swagger-parser 2.1.22)
cd tool
mvn -o -q compile exec:java "-Dexec.args=<abs path to specs> <abs path to tool\validation.json>"

# 3. Rewrite manifest.csv with the parser's authoritative results
python fetch_dataset.py finalize
```

- `validation.json` is produced by the probe and kept as evidence that every
  row parses.
- `download` reuses the cached index under `.cache/` unless `--force-index` is
  given; `--force` re-downloads spec files.
- Specs are stored exactly as received; the fetcher re-reads and re-parses the
  stored bytes before including a file.

## Status

**Dataset ready for ground-truth labeling.** Collection is complete; all specs
parse through the analyzer's own parser unmodified; the paper-mapped rules have
not been run on this corpus yet, and no ground-truth labels exist here.