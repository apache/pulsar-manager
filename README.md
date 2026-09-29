# Apache Pulsar Manager (no longer maintained)

> [!WARNING]
> Apache Pulsar Manager is no longer maintained, and using it is not recommended.
> To find alternative solutions, see the [Administration UI documentation](https://pulsar.apache.org/docs/administration-ui).

## Project status

Apache Pulsar Manager is no longer maintained by the Apache Pulsar project. It will not receive new
releases, bug fixes or updates for newer Pulsar versions, and its documentation has been removed from
the current Pulsar documentation.

We do not recommend using Pulsar Manager for new deployments. If you are running it today, plan to
move to an alternative solution and remove the Pulsar Manager deployment. If you deployed it with
the Apache Pulsar Helm chart, disable the `pulsar_manager` component, which is already disabled by
default in current chart versions.

## What Pulsar Manager was

Pulsar Manager was a web-based GUI for managing and monitoring Apache Pulsar clusters. It consisted of
a frontend web application and a backend service that connected to the brokers and bookies of one or
more Pulsar clusters. It offered views for tenants, namespaces, topics, subscriptions, clusters and
brokers, and a way to issue admin operations from the browser.

## Alternatives

To find alternative solutions for managing Apache Pulsar, go to the
[Administration UI documentation](https://pulsar.apache.org/docs/administration-ui).

## Source code

The source code stays available in this repository for reference, but it is not maintained. Issues and
pull requests for Pulsar Manager are no longer accepted.

For questions about managing Apache Pulsar, use the
[Apache Pulsar community channels](https://pulsar.apache.org/community/).

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
