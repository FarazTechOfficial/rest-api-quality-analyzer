# Results

Analysis outputs live here. Two kinds of files exist:

- `raw/` — one JSON file per API per run, written by
  `experiments/run_experiment.py` from the Analyzer Service response.
- `summary.json` / `experiment-summary.md` — the aggregated, dated run
  summary, regenerated on every `python experiments/run_experiment.py`.

## Standalone export

```bash
curl http://localhost:8082/api/reports/{analysisId}/export > results/raw/my-api.json
```

## Honesty note

Every number in this directory is produced at run time by the experiment
runner against the live Analyzer Service. The fixture corpus under
`sample-apis/` is a **development corpus**, not the paper's dataset
(GCP/OpenStack/OCCI 1.2). Do not cite these numbers as a reproduction of
Petrillo et al. (ICSOC 2016) — see `docs/research-replication.md`.