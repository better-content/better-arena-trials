# Better Arena Trials

## Native fixture verification

`verifyFast` includes absent-settings/preserve-existing and strict ERROR/FATAL guard regressions.
Source GameTest bootstrap reads ForgeGradle's actual RunConfig directory, creates defaults-only
`server.properties` only if absent, and preserves existing settings. Native ERROR/FATAL rejects the
full gate even when test counts and process exit indicate success; no malformed native case is waived.

## Playtest logging

`bc.playtest.better_arena_trials.match` observes trial safe-ground rejection, native wave
insertion/rollback, duel start, snapshot restoration, reward-stock commit and terminal cleanup.
Native participant UUIDs, dimension/game tick and available totem/match origin correlate these
outcomes. Duel start includes the second participant UUID; terminal cleanup and known-match
restoration carry the existing match start tick. Restoration passes the already available match
origin/dimension, or the native return origin when available; login recovery does not resolve a
missing match or read extra snapshot payload solely for logging and marks its start tick `UNKNOWN`. These bounded
causal UUIDs/positions are intentional. No replay/player names or inventory payloads are logged. DEBUG/TRACE share
120/minute (burst 20), 256 aggregation keys and 2,048-character records, with 30-second coalesced
summaries. Startup INFO reports loaded version/mode/limits and provider presence; source identity
is `UNKNOWN` when unavailable. Stop/disconnect clears transient diagnostics without changing
matches, snapshots, saves or the process allowance. The required Traces boundary calls its existing
typed public APIs, not reflection. Archive append is reported only after provider publication returns;
missing records remain distinct from real count/read exceptions, which retain fixed ERROR plus cause
outside detail suppression while preserving the existing empty-gallery fallback. No archive ID or
compressed payload is logged. Pure emitter/contracts and the native typed archive round-trip/missing
record case supplement match/reward tests; logs alone are not durable-storage or runtime acceptance.

## Scope

Better Arena Trials places standalone totems in the overworld. Each totem uses the surrounding natural
terrain for player duels and solo encounters, with three shared one-time rewards and a replay
gallery. Trials provide a solo route to the same reward stock. Duel and trial kits are loaned for
the match and the player's inventory and experience are restored afterward.

The Champion's Blade and Warden's Axe are powerful one-of-a-kind weapons. The Duelist's Sigil
grants Strength II for 30 seconds and recharges after five minutes. A victory earns the right to
claim one reward still available at that arena; replay viewing does not grant rewards.

Better Arena Trials stores duel replays through Player Traces in separate append-only files under
`data/better_player_traces/arena_duels/`, keyed by dimension and arena position. Recordings store motion
only. Player names appear only when both duelists enabled `/arena consent-names` before the duel.
Replays are visual echoes and cannot attack or deal damage.

Player Traces is a required runtime dependency. Totems generate in broad, dry overworld biomes
without changing the surrounding terrain. Fights take place within 48 blocks of the totem.
Players who leave are warned and have 10 seconds to return before forfeiting or ending a trial.

When Death's Door is installed, Arena registers a typed read-only life-record eligibility provider.
Active matches and defeated participants awaiting respawn are contained: match activity does not
advance ordinary-world life counters, and contained deaths retain the same world life ID. The
finishing tick remains excluded from collection so the native credited kill for the final trial mob
cannot leak into ordinary-world records after the match is removed. This collection guard does not
contain later ordinary deaths, award records, or change match, kit, damage or reward mechanics.
Source builds stage Death's Door before Player Traces and Arena; no reflection or guessed persistent
player tags are used for this integration.

Separately from life-record eligibility, the existing kit/reward item deferred register is wired
into the mod event bus. Native match-start coverage exposed the previously missing registration;
the repair registers the already-authored Champion's Blade, Warden's Axe and Duelist's Sigil under
their unchanged identifiers. It introduces no new items and changes no kit, reward or match rules.
