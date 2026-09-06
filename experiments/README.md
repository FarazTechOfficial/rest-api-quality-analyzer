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

The default corpus is a **development fixture set**, not the paper's dataset.
A replication dataset for Petrillo et al. (ICSOC 2016) — the paper's three
APIs or a documented replacement, plus manual ground-truth labels for the
non-automatable practices — does not yet exist. Until it does, experiment
outputs are tool self-checks; the paper's research questions remain
**RESULTS NOT YET AVAILABLE**.