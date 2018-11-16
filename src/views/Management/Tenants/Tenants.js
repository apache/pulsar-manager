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
import { Link } from 'react-router-dom'
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

class Tenants extends Component {

  state = {
    loading: true,
    tenants: [],
  };

  componentDidMount() {
    this._asyncRequest = API.getTenants().then(
      tenants => {
        this._asyncRequest = null;
        this.setState({ loading: false, tenants: tenants.data });
    });
  }

  componentWillUnmount() {
    if (this._asyncRequest) {
      this._asyncRequest.cancel();
    }
  }

  handleDeleteTenant(tenant, event) {
    API.deleteTenant(tenant).then(
      ignored => {
        this.props.history.push('/');
      }
    )
    .catch(
      error => {
        alert(`Failed to delete tenant '${tenant}' : ${error}`);
      }
    );
    event.preventDefault();
  }

  renderTenants() {
    return (
      <Table responsive size="sm">
        <thead>
        <tr>
          <th>Name</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        {this.state.tenants.map(tenant => (
          <tr key={tenant}>
            <td>{tenant}</td>
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

  listTenants() {
    return (
      this.state.tenants.length > 0 ? this.renderTenants() : (
        !this.state.loading && <p class="card-text">No tenants in this cluster</p>
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
                Tenants
                <div className="card-header-actions">
                  <Link to="/management/tenants/create" color="success">
                    <i className="fa fa-plus"></i>
                  </Link>
                </div>
              </CardHeader>
              <CardBody>
                {this.listTenants()}
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }

}

export default Tenants;
