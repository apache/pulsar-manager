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
  Form,
  FormGroup,
  FormText,
  Input,
  Label,
  Row,
  Table
} from 'reactstrap';
import { AppSwitch } from '@coreui/react'

import API from '../../../api'
import PULSAR from '../../../utils'

class CreateNamespace extends Component {

  constructor(props) {
    super(props);
    this.tenant = this.props.match.params.tenant;
    this.state = {
      namespace: '',
      policies: {}
    };
    this.handleNameChange = this.handleNameChange.bind(this);
    this.handleCreateNamespace = this.handleCreateNamespace.bind(this);
  }

  handleNameChange(event) {
    this.setState({
      namespace: event.target.value,
      policies: this.state.policies,
    });
  }

  handleCreateNamespace(event) {
    API.createNamespace(this.tenant, this.state.namespace, this.state.policies).then(
      ignored => {
        this.props.history.push(PULSAR.getTenantUrl(this.tenant));
      }
    )
    .catch(
      error => {
        alert(`Failed to create namespace '${this.tenant}/${this.state.namespace}' : ${error}`);
      }
    );
    event.preventDefault();
  }

  render() {
    return (
      <div className="animated fadeIn">
        <Row>
          <Col>
            <Card>
              <CardHeader>
                <strong>Create Namespace for Tenant {this.tenant}</strong>
              </CardHeader>
              <CardBody>
                <Form onSubmit={this.handleCreateNamespace} method="post" className="form-horizontal">
                  <FormGroup row>
                    <Col md="3">
                      <Label htmlFor="name">Name</Label>
                    </Col>
                    <Col xs="12" md="9">
                      <Input
                        type="text"
                        id="name"
                        value={this.state.namespace}
                        onChange={this.handleNameChange}
                        placeholder="Namespace name"
                        required/>
                      <FormText className="help-block">Please enter namespace name</FormText>
                    </Col>
                  </FormGroup>
                  <FormGroup>
                    <Button type="submit" size="sm" color="primary" onClick={this.createTenant}>
                      <i className="fa fa-dot-circle-o"></i> Create
                    </Button>
                    <Link to={PULSAR.getTenantUrl(this.tenant)}>
                      <Button type="reset" size="sm" color="danger">
                        <i className="fa fa-ban"></i> Cancel
                      </Button>
                    </Link>
                  </FormGroup>
                </Form>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }

}

export default withRouter(CreateNamespace);
