# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

ksoup is a small Kotlin DSL on top of jsoup, built with Gradle (Kotlin DSL).

Prefer simple solutions and few dependencies. Don't add new deps without asking.

## Build and test

```bash
mise exec -- ./gradlew test
```

- The build requests a JDK 21 toolchain and `mise.toml` pins `zulu-21`. Always run Gradle
  through mise (`mise exec -- ...`, run `mise install` first if the JDK is missing).
- Tests that need HTTP extend `StaticWebTest`.

## Process

1. **Plan first for non-trivial changes.** Read the relevant code, then present a short
   plan: what exists, what is wrong with it, the proposed change, alternatives rejected,
   and how it will be verified. Simpler is better. Wait for approval.
2. **Every modification task goes on a new branch** unless the user says to work on `master`. Don't commit until asked.
3. **Verify.** Follow the build and test procedure, and report the actual result.
   Check claims (for example JDK or dependency availability) rather than asserting them from memory.
4. **Keep commits focused.** Stage only the hunks that belong to the change
   (`git add -p`); unrelated uncommitted edits stay out of it or get their own commit when asked.
5. **Merge and push only when asked.** The branch lands on `master` as a linear history,
   with no merge commit:
    - `git fetch`, then rebase the branch onto `master`.
    - `git merge --ff-only <branch>` on `master`, then `git push`.
    - If local changes block the checkout, stash, switch, merge, then pop.
    - Report what was pushed, including any earlier unpushed commits that went with it.
6. **Commit messages:** a short imperative subject, a body that says why when it isn't
obvious, and the co-author trailer. Use dev shorthand where appropriate. Be concise and lapidary.
7. **Don't touch unrelated things** (dependency versions, formatting, other files) while
   making a change; mention them instead.
