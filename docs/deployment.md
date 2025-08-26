# Deploy IL application to production

The Safety Net Illinois team uses GitHub actions to trigger a new deploy in Aptible. This run-book focus on how to deploy to our production environment.

# Prerequisites

- You have write access to our GitHub repository - [il-gcc-form-flow](https://github.com/codeforamerica/il-gcc-form-flow).
- Access to the [Illinois-CCAP-Team Jira](https://codeforamerica.atlassian.net/jira/software/c/projects/CCAP/boards/7) board.

# Standard Deployment

If you want to deploy the latest version of `main` to Production, follow the steps below in order for:

1. **Create a GitHub release**
2. **Manually trigger a GitHub action run**
3. **Move Jira tickets from Ready to Done**

## Create a GitHub release

- In GitHub, [create a](https://github.com/codeforamerica/il-gcc-form-flow/tags) [`New Release`](https://github.com/codeforamerica/il-gcc-form-flow/releases/new).

- Choose a new tag in the style of [`year.month.day`](http://year.month.day) (for example: `2024.07.30`)
    - If you’re doing more than one deploy in a single day, add an incremental digit at the end (for example: `2024.07.30` then `2024.07.30-1`, then `2024.07.30-2`)
- Title the release with the same value as the tag
- Add information about what is changing in the release
    - GitHub has a feature that automatically generates notes of the changes between the last release and the new release.
- Deselect `Set as a pre-release`
- Select `Set as the latest release`
- Click `Publish release`

- ℹ️ Here’s an example of what a release could look like before you release it:

  ![Untitled](/docs/images/deployment-releasenotes.png)


## Manually trigger a GitHub action run

- Now that the release (and tag) is made, we will use that to deploy from
- Navigate to [the `Deploy to Aptible` Prod GitHub Action](https://github.com/codeforamerica/il-gcc-form-flow/actions/workflows/deploy-to-prod.yaml)
- Click on `Run workflow`
    - Can’t see `Run workflow`?

        <aside>
        ⚠️ If you don’t see `Run workflow` you don’t have edit access to the repository! Talk to your fellow engineers to get the right permissions in GitHub

        </aside>

- Select the tag you created
- Click `Run workflow`

![Untitled](/docs/images/deployment-runworkflow.png)

- Wait for the action to finish, it takes ~6 minutes.
- (Optional) You can visit [`https://www.getchildcareil.org/actuator/info`](https://www.getchildcareil.org/actuator/info) to see the time of the last deployment to double confirm the deployment worked

## Move Jira tickets from `Ready` to `Done`

When work in on the `main` branch and has completed acceptance testing, they will be in the `Ready` column. Once a production deploy is successfully complete, move all of the tickets into the `Done` column.

![Untitled](/docs/images/deployment-jira.png)

<br/><br/>

# Hotfix Deployment

The use case/scenario for a Hotfix is that the most recent **Standard Deployment** tag is stable, there are changes in `main` that are not ready for release to Production, and some incremental change between “what is already in Production” and “everything in `main`" needs to be deployed to Production. This needed change could be a bug fix, a subset of “everything in `main`", etc.

Assuming a branch, either off of `main` or (more likely) the most recent **Standard Deployment** tag, has been created and has the proposed change(s) committed and merged in:

1. Deploy the HEAD of the branch to Staging using the `Deploy to Aptible Staging` Github Action
    1. Click `Run workflow` —> Click `main` —> select the Hotfix branch
2. Deploy the HEAD of the branch to QA using the `Deploy to AWS QA` Github Action
    1. Click `Run workflow` —> Click `main` —> select the Hotfix branch
3. Verify the proposed fixes work on both Staging and QA as needed
    1. Repeat the above steps, as necessary, until the HEAD of Hotfix branch passes acceptance and is ready for deployment to Production
4. **Create a GitHub release**, except the branch dropdown should be the Hotfix branch and not `main`:

   ![Untitled](/docs/images/deployment-hotfixbranches.png)

5. Follow steps #1 and #2, but select the newly created tag instead of the name of the Hotfix branch
6. Optionally verify, again, that the proposed fixes work on both Staging and QA as needed. (Time may be of the essence, and re-testing might not be an option here.)
7. Follow the steps for to **Manually trigger a GitHub action run**, using the tag created.
8. **Move Jira tickets from Ready to Done**
9. If the fix(es) from the Hotfix branch are not already in `main`, be sure to merge them into `main` so the next **Standard Deployment** does not regress the fix(es)
