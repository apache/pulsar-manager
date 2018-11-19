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

class Tenant extends Component {

  constructor(props) {
    super(props);
    this.tenant = this.props.match.params.tenant;
    this.state = {
      loading: true,
      namespaces: [],
    };
  }

  componentDidMount() {
    this.setState({
      loading: true,
      namespaces: [],
    })
    this._asyncRequest = API.getNamespaces(this.tenant).then(
      namespaces => {
        this._asyncRequest = null;
        this.setState({
          loading: false,
          namespaces: namespaces.data,
        });
    });
  }

  handleDeleteNamespace(fully_qualified_namespace, event) {
    API.deleteNamespace(fully_qualified_namespace).then(
      ignored => {
        this.props.history.push('/');
      }
    )
    .catch(
      error => {
        alert(`Failed to delete namespace '${fully_qualified_namespace}' : ${error}`);
      }
    );
    event.preventDefault();
  }

  renderNamespaces() {
    return (
      <Table responsive size="sm">
        <thead>
        <tr>
          <th>Name</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        {this.state.namespaces.map(PULSAR.getNamespaceName).map(ns => (
          <tr key={ns.fully_qualified_namespace}>
            <td>
            <Link to={PULSAR.getTenantUrl(ns.tenant)}>{ns.tenant}</Link>
            <strong>/</strong>
            <Link to={PULSAR.getNamespaceUrl(ns.tenant, ns.namespace)}>{ns.namespace}</Link>
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

  listNamespaces() {
    return (
      this.state.namespaces.length > 0 ? this.renderNamespaces() : (
        !this.state.loading && <p className="card-text">No namespaces in this cluster</p>
      )
    );
  }

  getCreateNamespaceUrl() {
    return `/management/tenant/${this.tenant}/namespaces/create`;
  }

  render() {
    return (
      <div className="animated fadeIn">
        <Row>
          <Col>
            <Card>
              <CardHeader>
                Tenant <Link to={PULSAR.getTenantUrl(this.tenant)}>
                {this.tenant}
                </Link>
                <div className="card-header-actions">
                  <Link to={this.getCreateNamespaceUrl()} color="success">
                    <i className="fa fa-plus"></i>
                  </Link>
                </div>
              </CardHeader>
              <CardBody>
                {this.listNamespaces()}
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }

}

export default withRouter(Tenant);
