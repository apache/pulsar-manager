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

class CreateTenant extends Component {

  constructor(props) {
    super(props)
    this.state = {
      value: ''
    };
    this.handleNameChange = this.handleNameChange.bind(this);
    this.handleCreateTenant = this.handleCreateTenant.bind(this);
  }

  handleNameChange(event) {
    this.setState({value: event.target.value});
  }

  handleCreateTenant(event) {
    API.createTenant(this.state.value).then(
      ignored => {
        this.props.history.push('/management/tenants');
      }
    )
    .catch(
      error => {
        alert(`Failed to create tenant '${this.state.value}' : ${error}`);
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
                <strong>Create Tenant</strong>
              </CardHeader>
              <CardBody>
                <Form onSubmit={this.handleCreateTenant} method="post" className="form-horizontal">
                  <FormGroup row>
                    <Col md="3">
                      <Label htmlFor="name">Name</Label>
                    </Col>
                    <Col xs="12" md="9">
                      <Input
                        type="text"
                        id="name"
                        value={this.state.value}
                        onChange={this.handleNameChange}
                        placeholder="Tenant name"
                        required/>
                      <FormText className="help-block">Please enter tenant name</FormText>
                    </Col>
                  </FormGroup>
                  <FormGroup>
                    <Button type="submit" size="sm" color="primary" onClick={this.createTenant}>
                      <i className="fa fa-dot-circle-o"></i> Create
                    </Button>
                    <Link to="/management/tenants">
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

export default withRouter(CreateTenant);
