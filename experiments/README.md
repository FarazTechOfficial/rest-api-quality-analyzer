# Experiments

This directory carries the reproducible experiment harness for the research-replication audit (see `docs/research-replication.md`).

## Runner

```bash
python experiments/run_experiment.py
```

- Reads a corpus (by default the `sample-apis/` fixtures in `experiments/run_experiment.py`, overridable via `experiments/apis.jsonc`).
- POSTs each OpenAPI spec to the Analyzer Service (`/api/analyze`, port 8081).
- Writes raw responses into `results/raw/` and an aggregated
  `results/summary.json` + `results/experiment-summary.md`.

## Protocol template (per run)

- **Date**: from `summary.json#generatedAt` (UTC)
- **API analyzed**: per `summary.json#apis[].{name,source}`
- **Analysis ID**: raw file per API, `apiName` field inside each raw report
- **Score**: per `summary.json#apis[].score`
- **Notes**: any deviation from the corpus definition, plus the rule version
  (git hash) used.

## Dataset status

A **real-world dataset now exists** under [`dataset/`](dataset/README.md):
37 distinct production API specifications collected from the APIs.guru
openapi-directory, each verified to parse through the analyzer's own parser
unchanged. It is the documented replacement for the **development fixture
set** (`sample-apis/`).

Ground-truth labels for the non-automatable practices do **not yet exist**, so
experiment outputs on the real dataset are still pending. Until labels and an
experiment run land, the paper's research questions remain **RESULTS NOT YET
AVAILABLE**.