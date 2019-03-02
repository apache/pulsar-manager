/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom'
import {
  Badge,
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Col,
  Collapse,
  Fade,
  Row,
  Table
} from 'reactstrap';
import { AppSwitch } from '@coreui/react'

import API from '../../../api'
import PULSAR from '../../../utils'

class Namespace extends Component {

  constructor(props) {
    super(props);
    this.tenant = this.props.match.params.tenant;
    this.namespace = this.props.match.params.namespace;
    this.state = {
      topics_loading: true,
      topics: [],
      partitioned_topics_loading: true,
      partitioned_topics: [],
    };
  }

  componentDidMount() {
    this._asyncGetTopicsRequest = API.getTopics(this.tenant, this.namespace).then(
      topics => {
        this._asyncGetTopicsRequest = null;
        this.setState({
          topics_loading: false,
          topics: topics.data,
          partitioned_topics_loading: this.state.partitioned_topics_loading,
          partitioned_topics: this.state.partitioned_topics,
        });
    });

    this._asyncGetPartitionedTopicsRequest = API.getPartitionedTopics(this.tenant, this.namespace).then(
      topics => {
        this._asyncGetPartitionedTopicsRequest = null;
        this.setState({
          topics_loading: this.state.topics_loading,
          topics: this.state.topics,
          partitioned_topics_loading: false,
          partitioned_topics: topics.data,
        });
    });
  }

  handleDeleteTopic(topic, is_partitioned, event) {
  }

  renderTopics() {
    return (
      <Table responsive size="sm">
        <thead>
        <tr>
          <th>Name</th>
          <th>Type</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        {PULSAR.normalizeTopics(this.state.topics, this.state.partitioned_topics).map(t => (
          <tr key={t.name}>
            <td>
            <Link to={PULSAR.getTopicUrl(this.tenant, this.namespace, t.name)}>
            {t.name}
            </Link>
            </td>
            {
              t.is_partitioned ? <b>partitioned</b> : <b>non-partitioned</b>
            }
            <td>
            </td>
            <td>
            <Button color="danger">
              <i className="fa fa-trash-o"></i>
            </Button>
            </td>
          </tr>
        ))}
        </tbody>
      </Table>
    )
  }

  listTopics() {
    return (
      this.state.topics.length > 0 || this.state.partitioned_topics.length > 0 ? this.renderTopics() : (
        !this.state.topics_loading && !this.state.partitioned_topics_loading && <p className="card-text">No topics in this namespace</p>
      )
    );
  }

  render() {
    return (
      <div className="animated fadeIn">
        <Row>
          <Col>
            <Card>
              <CardHeader>
                Namespace <Link to={PULSAR.getTenantUrl(this.tenant)}>
                {this.tenant}
                </Link>/<Link to={PULSAR.getNamespaceUrl(this.tenant, this.namespace)}>
                {this.namespace}
                </Link>
                <div className="card-header-actions">
                  <Link to={PULSAR.getCreateTopicUrl(this.tenant, this.namespace)} color="success">
                    <i className="fa fa-plus"></i>
                  </Link>
                </div>
              </CardHeader>
              <CardBody>
                {this.listTopics()}
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }

}

export default withRouter(Namespace);
