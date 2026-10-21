# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

ksoup is a small Kotlin DSL on top of jsoup (`src/main`), built with Gradle (Kotlin DSL).
Tests live in `src/test` and use `kotlin("test")` with JUnit 5. Prefer simple solutions
and few dependencies.

## Build and test

```bash
mise exec -- ./gradlew test
```

- The build requests a JDK 21 toolchain and `mise.toml` pins `zulu-21`. Always run Gradle
  through mise (`mise exec -- ...`, run `mise install` first if the JDK is missing). Don't
  edit `JavaLanguageVersion` or use another installed JDK to work around a missing one.
- Tests that need HTTP extend `StaticWebTest`, which runs the JDK's built-in
  `com.sun.net.httpserver.HttpServer` on a random free port. Do not add a test server
  dependency. Use `testUrl(path)` for URLs, `staticContentResolver` to map paths to
  classpath resources, and `redirects` to answer with a 302.
- Don't add explicit JUnit dependencies; `kotlin("test")` plus `useJUnitPlatform()` is enough.

## Process

1. **Plan first for non-trivial changes.** Read the relevant code, then present a short
   plan: what exists, what is wrong with it, the proposed change, alternatives rejected,
   and how it will be verified. Simpler is better. Wait for approval.
2. **Every modification task goes on a new branch** in the current worktree, unless the
   user says to work on `master`. Don't commit until asked.
3. **Verify.** Run `./gradlew test` and report the actual result. Check claims (for
   example JDK or dependency availability) rather than asserting them from memory.
4. **Keep commits focused.** Stage only the hunks that belong to the change
   (`git add -p`); unrelated uncommitted edits, such as a version bump, stay out of it
   or get their own commit when asked.
5. **Integrate when asked to "rebase-merge and push":**
   - `git fetch`, then rebase the branch onto `master`.
   - Fast-forward `master` (`git merge --ff-only <branch>`), so history stays linear
     with no merge commit.
   - `git push`.
   - If the checkout is blocked by local changes, stash, switch, merge, then pop.
   - Report what was pushed, including any earlier unpushed commits that went with it.
6. **Commit messages:** a short imperative subject, a body that says why when it isn't
   obvious, and the co-author trailer required by the session.
7. **Don't touch unrelated things** (dependency versions, formatting, other files) while
   making a change; mention them instead.
